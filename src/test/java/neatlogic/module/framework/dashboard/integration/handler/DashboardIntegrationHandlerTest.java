package neatlogic.module.framework.dashboard.integration.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.integration.core.IRequestFrom;
import neatlogic.framework.integration.core.IntegrationDefaultValueUtil;
import neatlogic.framework.integration.core.IntegrationHandlerBase;
import neatlogic.framework.integration.core.IntegrationPatternUtil;
import neatlogic.framework.integration.dto.IntegrationResultVo;
import neatlogic.framework.integration.dto.IntegrationVo;
import neatlogic.framework.integration.dto.PatternVo;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/** 验证大屏集成固定规范和真实输出校验，不执行外部 HTTP 或审计文件写入。 */
public class DashboardIntegrationHandlerTest {
    private final DashboardIntegrationHandler handler = new DashboardIntegrationHandler();

    /** 固定输入只保留查询参数，表头及六个子项统一属于输出规范，同时允许扩展自定义参数。 */
    @Test
    public void declaresRequiredOutputHeaderAndEditableQueryPatterns() {
        assertEquals("DashboardIntegrationHandler", handler.getHandler());
        assertEquals(Integer.valueOf(1), handler.hasInputPattern());
        assertEquals(Integer.valueOf(1), handler.hasOutputPattern());
        assertEquals(Integer.valueOf(1), handler.allowEditInputParam());
        assertEquals(Integer.valueOf(1), handler.allowEditOutputParam());
        List<PatternVo> input = handler.getInputPattern();
        assertEquals(set("conditionList", "sortList", "currentPage", "pageSize", "limit", "needPage", "isExpired"), names(input));
        assertFalse(names(input).contains("theadList"));
        PatternVo header = find(handler.getOutputPattern(), "theadList");
        assertEquals("output", header.getMode());
        assertEquals(ApiParamType.JSONARRAY.getValue(), header.getType());
        assertEquals(Integer.valueOf(1), header.getIsRequired());
        assertEquals(set("key", "title", "type", "isCondition", "inputType", "config"), names(header.getChildren()));
        assertEquals(Integer.valueOf(1), find(header.getChildren(), "key").getIsRequired());
        assertEquals(Integer.valueOf(1), find(header.getChildren(), "title").getIsRequired());
        assertEquals(ApiParamType.JSONOBJECT.getValue(), find(header.getChildren(), "config").getType());
        for (PatternVo child : header.getChildren()) {
            assertEquals("output", child.getMode());
            assertEquals(Integer.valueOf(set("key", "title").contains(child.getName()) ? 1 : 0), child.getIsRequired());
            String expectedType = "config".equals(child.getName()) ? ApiParamType.JSONOBJECT.getValue()
                    : "isCondition".equals(child.getName()) ? ApiParamType.INTEGER.getValue() : ApiParamType.STRING.getValue();
            assertEquals(expectedType, child.getType());
        }
        for (String name : Arrays.asList("currentPage", "pageSize", "limit")) {
            assertEquals(ApiParamType.INTEGER.getValue(), find(input, name).getType());
        }
        assertEquals(ApiParamType.BOOLEAN.getValue(), find(input, "needPage").getType());
        assertFalse(names(input).contains("datasourceType"));
        assertFalse(names(input).contains("dataSourceId"));
    }

    /** 固定输出包含默认表头、数据行和四个分页整数，远端无需重复返回可由默认值补齐的表头。 */
    @Test
    public void requiresRowsAndAllPaginationProperties() {
        List<PatternVo> output = handler.getOutputPattern();
        assertEquals(set("theadList", "tbodyList", "currentPage", "pageSize", "rowNum", "pageCount"), names(output));
        for (PatternVo pattern : output) {
            assertEquals("output", pattern.getMode());
            assertEquals(Integer.valueOf(1), pattern.getIsRequired());
            assertEquals(set("theadList", "tbodyList").contains(pattern.getName()) ? ApiParamType.JSONARRAY.getValue() : ApiParamType.INTEGER.getValue(), pattern.getType());
        }
    }

    /** 合并后的默认值只属于当前集成，固定规范与其他集成不能被修改。 */
    @Test
    public void mergesConfiguredHeaderDefaultsWithoutMutatingPatternOrConfig() {
        JSONObject config = config();
        JSONObject before = JSON.parseObject(config.toJSONString());
        List<PatternVo> merged = IntegrationPatternUtil.merge(handler, config, "output");
        JSONObject defaults = find(merged, "theadList").getDefaultConfig();
        assertTrue(defaults.getBooleanValue("enabled"));
        assertEquals("duration", defaults.getJSONArray("value").getJSONObject(0).getString("key"));
        defaults.getJSONArray("value").getJSONObject(0).put("key", "changed-in-test");
        assertEquals(before, config);
        assertNull(find(handler.getOutputPattern(), "theadList").getDefaultConfig());
        assertNull(find(IntegrationPatternUtil.merge(handler, new JSONObject(), "output"), "theadList").getDefaultConfig());
    }

    /** 真实输入准备不注入表头；公共输出补齐工具填充缺失表头，并保持输入及持久化配置不变。 */
    @Test
    public void appliesHeaderDefaultsOnlyThroughExistingOutputPipeline() {
        ExposedHandler exposed = new ExposedHandler();
        IntegrationVo source = new IntegrationVo();
        source.setConfig(config().toJSONString());
        source.setParamObj(new JSONObject().fluentPut("limit", 0).fluentPut("needPage", false));
        JSONObject before = JSON.parseObject(source.getConfig().toJSONString());
        exposed.prepare(source);
        assertFalse(source.getParamObj().containsKey("theadList"));
        assertEquals(set("limit", "needPage"), source.getParamObj().keySet());
        IntegrationResultVo response = result(validResult().toJSONString());
        assertFalse(JSON.parseObject(response.getTransformedResult()).containsKey("theadList"));
        IntegrationDefaultValueUtil.applyOutput(exposed, source.getConfig(), response);
        JSONArray headers = JSON.parseObject(response.getTransformedResult()).getJSONArray("theadList");
        assertEquals(1, headers.size());
        assertEquals("duration", headers.getJSONObject(0).getString("key"));
        assertFalse(headers.getJSONObject(0).containsKey("config"));
        assertEquals(0, source.getParamObj().getIntValue("limit"));
        assertFalse(source.getParamObj().getBooleanValue("needPage"));
        exposed.validate(response);
        headers.getJSONObject(0).put("key", "changed-in-result");
        assertEquals(before, source.getConfig());
    }

    /** 通用默认值规则仍拒绝显式 null 对象，避免大屏功能悄悄放宽其他集成配置。 */
    @Test
    public void retainsExistingRejectionForExplicitNullControlConfig() {
        JSONObject config = config();
        config.getJSONObject("param").getJSONArray("builtinDefaultList").getJSONObject(0)
                .getJSONObject("defaultConfig").getJSONArray("value").getJSONObject(0).put("config", null);
        IntegrationVo source = new IntegrationVo();
        // WriteMapNullValue 保留故意构造的非法显式 null，不让序列化静默省略它。
        source.setConfig(JSON.toJSONString(config, com.alibaba.fastjson.serializer.SerializerFeature.WriteMapNullValue));
        ExposedHandler exposed = new ExposedHandler();
        exposed.prepare(source);
        assertFalse(source.getParamObj().containsKey("theadList"));
        assertThrows(ApiRuntimeException.class, () -> IntegrationDefaultValueUtil.validate(exposed, source.getConfig()));
        assertThrows(ApiRuntimeException.class, () -> IntegrationDefaultValueUtil.applyOutput(exposed, source.getConfig(), result(validResult().toJSONString())));
    }

    /** 已删除的旧输入表头一旦启用，公共保存校验、输入准备和输出补齐均拒绝，不因存在合法输出默认值而绕过。 */
    @Test
    public void rejectsEnabledLegacyInputDefaultsAlongsideOutputDefaults() {
        JSONObject config = JSON.parseObject(config().toJSONString());
        JSONArray entries = config.getJSONObject("param").getJSONArray("builtinDefaultList");
        JSONObject legacy = JSON.parseObject(entries.getJSONObject(0).toJSONString());
        legacy.put("mode", "input");
        legacy.getJSONObject("defaultConfig").getJSONArray("value").getJSONObject(0).put("key", "legacy");
        entries.add(legacy);
        JSONObject before = JSON.parseObject(config.toJSONString());
        ExposedHandler exposed = new ExposedHandler();
        IntegrationVo source = new IntegrationVo();
        source.setConfig(config.toJSONString());
        source.setParamObj(new JSONObject());
        assertThrows(ApiRuntimeException.class, () -> IntegrationDefaultValueUtil.validate(exposed, config));
        assertThrows(ApiRuntimeException.class, () -> exposed.prepare(source));
        assertThrows(ApiRuntimeException.class, () -> IntegrationDefaultValueUtil.applyOutput(exposed, config, result(validResult().toJSONString())));
        assertEquals(before, config);
        assertFalse(source.getParamObj().containsKey("theadList"));
    }

    /** 合法空数据、空对象行以及普通业务对象行均通过结构校验。 */
    @Test
    public void acceptsEmptyDataAndObjectRowsWithoutRequiringReturnedHeader() {
        for (String rows : Arrays.asList("[]", "[{}]", "[{\"name\":\"作业A\",\"duration\":0},{\"name\":null}]")) {
            JSONObject body = validResult();
            body.put("tbodyList", JSON.parseArray(rows));
            handler.validate(result(body.toJSONString()));
        }
    }

    /** 无返回、非 JSON、非对象和缺少任一固定输出都必须失败，不能伪造空成功。 */
    @Test
    public void rejectsMissingMalformedAndIncompleteOutput() {
        for (String content : Arrays.asList(null, "", " ", "not-json", "[]", "null", "{}")) {
            assertThrows(ApiRuntimeException.class, () -> handler.validate(result(content)));
        }
        for (String key : set("tbodyList", "currentPage", "pageSize", "rowNum", "pageCount")) {
            JSONObject body = validResult();
            body.remove(key);
            assertThrows(ApiRuntimeException.class, () -> handler.validate(result(body.toJSONString())));
        }
    }

    /** 数据行必须是对象数组，空行对象可用，但 null、字符串、数组或数字行均拒绝。 */
    @Test
    public void rejectsNonArrayBodiesAndNonObjectRows() {
        for (Object rows : Arrays.asList(null, new JSONObject(), "[]", JSON.parseArray("[null]"),
                JSON.parseArray("[1]"), JSON.parseArray("[\"bad\"]"), JSON.parseArray("[[]]"))) {
            JSONObject body = validResult();
            body.put("tbodyList", rows);
            assertThrows(ApiRuntimeException.class, () -> handler.validate(result(JSON.toJSONString(body,
                    com.alibaba.fastjson.serializer.SerializerFeature.WriteMapNullValue))));
        }
    }

    /** 分页字段按整数结构校验，字符串、浮点、布尔及 null 均不能静默转换通过。 */
    @Test
    public void rejectsNonIntegerPaginationValues() {
        for (String key : set("currentPage", "pageSize", "rowNum", "pageCount")) {
            for (Object invalid : Arrays.asList("1", 1.5, true, null)) {
                JSONObject body = validResult();
                body.put(key, invalid);
                assertThrows(ApiRuntimeException.class, () -> handler.validate(result(JSON.toJSONString(body,
                        com.alibaba.fastjson.serializer.SerializerFeature.WriteMapNullValue))));
            }
        }
    }

    /** 发送方法仍继承框架实现，原调用审计、转换与异常记录链路没有被大屏规范替换。 */
    @Test
    public void retainsFrameworkSendAndAuditPipeline() throws Exception {
        assertEquals(IntegrationHandlerBase.class,
                DashboardIntegrationHandler.class.getMethod("sendRequest", IntegrationVo.class, IRequestFrom.class).getDeclaringClass());
    }

    /** 构造固定输出默认值；省略没有内容的 config 符合现有通用保存规则。 */
    private static JSONObject config() {
        JSONObject entry = new JSONObject().fluentPut("handler", "DashboardIntegrationHandler").fluentPut("mode", "output")
                .fluentPut("name", "theadList").fluentPut("defaultConfig", new JSONObject().fluentPut("enabled", true)
                        .fluentPut("value", JSON.parseArray("[{\"key\":\"duration\",\"title\":\"耗时\"}]")));
        return new JSONObject().fluentPut("param", new JSONObject().fluentPut("builtinDefaultList", new JSONArray(Collections.singletonList(entry))));
    }

    /** 输出结构校验独立于网络，提供一组完整合法分页属性。 */
    private static JSONObject validResult() {
        return new JSONObject().fluentPut("tbodyList", new JSONArray()).fluentPut("currentPage", 1)
                .fluentPut("pageSize", 20).fluentPut("rowNum", 0).fluentPut("pageCount", 0);
    }

    /** 转换后的结果即为规范校验对象，不生成外部请求或凭据。 */
    private static IntegrationResultVo result(String transformed) {
        IntegrationResultVo result = new IntegrationResultVo();
        result.setStatusCode(200);
        result.setTransformedResult(transformed);
        return result;
    }

    /** 根据固定参数名定位定义，缺少时直接使测试失败。 */
    private static PatternVo find(List<PatternVo> list, String name) {
        return list.stream().filter(item -> name.equals(item.getName())).findFirst().orElseThrow(AssertionError::new);
    }

    /** 比较参数集合时不依赖展示顺序。 */
    private static Set<String> names(List<PatternVo> list) {
        return list.stream().map(PatternVo::getName).collect(Collectors.toSet());
    }

    /** 用无序集合表达严格的字段边界。 */
    private static Set<String> set(String... names) {
        return new HashSet<>(Arrays.asList(names));
    }

    /** 仅暴露已有受保护输入准备方法，不增加生产测试接口或替换默认值逻辑。 */
    private static class ExposedHandler extends DashboardIntegrationHandler {
        /** 子类沿用生产标识，使默认值关联到正确的固定规范。 */
        @Override public String getHandler() { return "DashboardIntegrationHandler"; }

        /** 执行真实默认值和参数校验管线，不进入网络发送阶段。 */
        private void prepare(IntegrationVo source) { prepareInputParam(source); }
    }
}
