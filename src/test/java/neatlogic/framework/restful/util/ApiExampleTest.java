package neatlogic.framework.restful.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.exception.type.ApiExampleInvalidException;
import neatlogic.framework.restful.annotation.Example;
import neatlogic.framework.restful.annotation.Input;
import neatlogic.framework.restful.annotation.Param;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentBase;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentFactory;
import neatlogic.framework.restful.core.privateapi.raw.RawApiComponentBase;
import neatlogic.framework.restful.core.privateapi.sse.SseApiComponentBase;
import neatlogic.framework.restful.dto.ApiVo;
import neatlogic.framework.restful.dto.ApiExampleVo;
import neatlogic.framework.restful.mcp.McpToolMetadataBuilder;
import neatlogic.framework.util.SpringContextUtil;
import org.junit.*;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.context.support.StaticMessageSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.Locale;

/** 验证场景声明到 API 帮助及模型工具定义的完整契约，不执行真实业务。 */
public class ApiExampleTest {
    private static Field contextField;
    private static Object originalContext;
    private static Locale originalLocale;

    /** 安装隔离的语言环境，避免测试依赖真实租户或数据库。 */
    @BeforeClass
    public static void installMessages() throws Exception {
        originalLocale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
        StaticApplicationContext context = new StaticApplicationContext();
        StaticMessageSource messages = new StaticMessageSource();
        for (Locale locale : new Locale[]{Locale.ENGLISH, Locale.CHINESE}) {
            messages.addMessage("scene.os", locale, locale.equals(Locale.ENGLISH) ? "OS Health" : "操作系统健康状态");
            messages.addMessage("scene.services", locale, "Service Health");
            messages.addMessage("scene.help", locale, "Replace the hostname with a registered node.");
            messages.addMessage("field.help", locale, "Select os or services; one category per call.");
            messages.addMessage("nf.api.example.mcparguments", locale, "Each example is params.arguments for tools/call.");
            messages.addMessage("nf.api.example.invalid", locale, "Invalid example {1} for {0}");
        }
        context.getBeanFactory().registerSingleton("messageSourceAccessor", new MessageSourceAccessor(messages));
        contextField = SpringContextUtil.class.getDeclaredField("ctx");
        contextField.setAccessible(true);
        originalContext = contextField.get(null);
        contextField.set(null, context);
    }

    /** 恢复全局状态，避免污染同一 JVM 的其他测试。 */
    @AfterClass
    public static void restoreMessages() throws Exception {
        contextField.set(null, originalContext);
        Locale.setDefault(originalLocale);
    }

    /** 重复注解按声明顺序返回，只有展示信息随语言变化。 */
    @Test
    public void translatesTitlesWithoutChangingArguments() {
        JSONArray english = new ObjectApi().help().getJSONArray("example");
        Assert.assertEquals(2, english.size());
        Assert.assertEquals("OS Health", english.getJSONObject(0).getString("title"));
        Assert.assertEquals("services", english.getJSONObject(1).getJSONObject("example").getString("type"));
        try {
            Locale.setDefault(Locale.CHINESE);
            JSONArray chinese = new ObjectApi().help().getJSONArray("example");
            Assert.assertEquals("操作系统健康状态", chinese.getJSONObject(0).getString("title"));
            Assert.assertEquals(english.getJSONObject(0).get("example"), chinese.getJSONObject(0).get("example"));
        } finally {
            Locale.setDefault(Locale.ENGLISH);
        }
    }

    /** Raw 和 SSE 的不同方法签名都读取相同的场景协议。 */
    @Test
    public void supportsRawAndSseWithoutOtherHelpAnnotations() {
        Assert.assertEquals(1, new RawApi().help().getJSONArray("example").size());
        Assert.assertEquals(1, new SseApi().help().getJSONArray("example").size());
    }

    /** 代理及继承的方法不能导致示例丢失。 */
    @Test
    public void supportsInheritedAndProxiedComponents() {
        Assert.assertEquals(2, new InheritedApi().help().getJSONArray("example").size());
        ProxyFactory factory = new ProxyFactory(new ObjectApi());
        factory.setProxyTargetClass(true);
        ObjectApi proxy = (ObjectApi) factory.getProxy();
        Assert.assertEquals(2, proxy.help().getJSONArray("example").size());
    }

    /** 无示例返回空列表，数组请求仍是单个场景内容。 */
    @Test
    public void handlesEmptyAndArrayExamples() throws Exception {
        Assert.assertTrue(ApiExampleUtil.getExamples(Samples.class.getMethod("empty")).isEmpty());
        JSONArray examples = ApiExampleUtil.getExamples(Samples.class.getMethod("array"));
        Assert.assertEquals(1, examples.size());
        Assert.assertTrue(examples.getJSONObject(0).get("example") instanceof JSONArray);
    }

    /** 非法 JSON 必须明确失败，不能悄悄发布缺失示例的帮助。 */
    @Test(expected = ApiExampleInvalidException.class)
    public void rejectsMalformedJson() throws Exception {
        ApiExampleUtil.getExamples(Samples.class.getMethod("invalid"));
    }

    /** JSON 标量不是合法的请求示例。 */
    @Test(expected = ApiExampleInvalidException.class)
    public void rejectsScalarJson() throws Exception {
        ApiExampleUtil.getExamples(Samples.class.getMethod("scalar"));
    }

    /** 模型在标准 description 和字段 Schema 中能读到场景和详细参数规则。 */
    @Test
    public void publishesExamplesAndParameterHelpToMcp() {
        String handler = ObjectApi.class.getName();
        PrivateApiComponentFactory.getComponentMap().put(handler, new ObjectApi());
        try {
            ApiVo api = new ApiVo();
            api.setHandler(handler);
            api.setToken("example/health/get");
            api.setName("Health");
            api.setDescription("Query registered node health.");
            JSONObject tool = McpToolMetadataBuilder.buildTool(api);
            String description = tool.getString("description");
            Assert.assertTrue(description.contains("OS Health"));
            Assert.assertTrue(description.contains("Service Health"));
            Assert.assertTrue(description.contains("Replace the hostname"));
            Assert.assertTrue(description.contains("params.arguments"));
            JSONObject type = tool.getJSONObject("inputSchema").getJSONObject("properties").getJSONObject("type");
            Assert.assertTrue(type.getString("description").contains("one category per call"));
            Assert.assertEquals(JSONArray.parseArray("[\"os\",\"services\"]"), type.getJSONArray("enum"));
            Assert.assertFalse(type.containsKey("pattern"));
            JSONObject meta = tool.getJSONObject("_meta");
            Assert.assertFalse(meta.containsKey("com.neatlogic/example"));
            Assert.assertFalse(meta.containsKey("com.neatlogic/examples"));
            Assert.assertEquals("example/health/get", meta.getString("com.neatlogic/token"));
            Assert.assertTrue(meta.containsKey("com.neatlogic/module"));
            String serializedTool = tool.toJSONString();
            Assert.assertEquals(1, serializedTool.split("OS Health", -1).length - 1);
            Assert.assertEquals(1, serializedTool.split("Service Health", -1).length - 1);
            JSONArray examples = McpToolMetadataBuilder.getExamples(api);
            Assert.assertEquals(new ObjectApi().help().getJSONArray("example"), examples);
            JSONArray calls = McpToolMetadataBuilder.getCallToolExamples(tool.getString("name"), examples);
            Assert.assertEquals(2, calls.size());
            for (int i = 0; i < examples.size(); i++) {
                JSONObject request = calls.getJSONObject(i).getJSONObject("example");
                Assert.assertEquals("tools/call", request.getString("method"));
                Assert.assertEquals(examples.getJSONObject(i).get("example"), request.getJSONObject("params").get("arguments"));
                Assert.assertEquals(examples.getJSONObject(i).getString("title"), calls.getJSONObject(i).getString("title"));
            }
            // 模拟帮助接口的完整响应，确保元数据、场景及调用示例不会产生共享引用。
            JSONObject help = new JSONObject(true);
            help.put("description", api.getDescription());
            help.put("meta", meta);
            help.put("example", examples);
            help.put("callToolExamples", calls);
            JSONObject response = new JSONObject(true);
            response.put("Return", help);
            String serializedHelp = response.toJSONString();
            Assert.assertFalse(serializedHelp, serializedHelp.contains("\"$ref\""));
            Assert.assertFalse(help.getString("description").contains("###"));
        } finally {
            PrivateApiComponentFactory.getComponentMap().remove(handler);
        }
    }

    /** 完整响应序列化时，每条调用示例必须自包含，嵌套参数也不能成为 $ref。 */
    @Test
    public void callExamplesRemainSelfContainedAfterResponseSerialization() {
        JSONArray examples = JSONArray.parseArray("[{\"title\":\"Nested Args\",\"description\":\"\",\"example\":{\"operationType\":\"tool\",\"param\":{\"paths\":[\"/tmp/app\"]},\"argument\":[\"hostname\"]}}]");
        JSONArray calls = McpToolMetadataBuilder.getCallToolExamples("autoexec.script.execrtool.exec", examples);
        JSONObject original = examples.getJSONObject(0).getJSONObject("example");
        JSONObject arguments = calls.getJSONObject(0).getJSONObject("example").getJSONObject("params").getJSONObject("arguments");
        Assert.assertNotSame(original, arguments);
        Assert.assertNotSame(original.get("param"), arguments.get("param"));
        Assert.assertNotSame(original.get("argument"), arguments.get("argument"));
        JSONObject help = new JSONObject(true);
        help.put("example", examples);
        help.put("callToolExamples", calls);
        JSONObject response = new JSONObject(true);
        response.put("Status", "OK");
        response.put("Return", help);
        String serialized = response.toJSONString();
        Assert.assertFalse(serialized, serialized.contains("\"$ref\""));
        JSONObject restored = JSONObject.parseObject(serialized).getJSONObject("Return")
                .getJSONArray("callToolExamples").getJSONObject(0).getJSONObject("example")
                .getJSONObject("params").getJSONObject("arguments");
        Assert.assertEquals(original, restored);
        arguments.getJSONObject("param").put("changed", true);
        Assert.assertFalse(original.getJSONObject("param").containsKey("changed"));
    }

    /** 同一请求参数可被模型直接放入 arguments，不需要剥离场景元信息。 */
    public static class ObjectApi extends PrivateApiComponentBase {
        public String getName() { return "Health"; }
        public String getToken() { return "example/health/get"; }
        @Input({@Param(name = "type", type = ApiParamType.STRING, rule = "os,services", desc = "Type", help = "field.help", isRequired = true)})
        @Example(title = "scene.os", description = "scene.help", example = "{\"hostname\":\"app-01\",\"type\":\"os\"}")
        @Example(title = "scene.services", example = "{\"hostname\":\"app-01\",\"type\":\"services\"}")
        public Object myDoService(JSONObject param) { throw new AssertionError("Must not execute"); }
    }

    /** 继承的服务方法仍由声明所在的真实类提供示例。 */
    public static class InheritedApi extends ObjectApi { }

    /** Raw 签名样本，只用于读取帮助。 */
    public static class RawApi extends RawApiComponentBase {
        public String getName() { return "Raw"; }
        public int needAudit() { return 0; }
        @Example(title = "scene.os", example = "{}")
        public Object myDoService(String param) { throw new AssertionError("Must not execute"); }
    }

    /** SSE 签名样本，只用于读取帮助。 */
    public static class SseApi extends SseApiComponentBase {
        public String getName() { return "SSE"; }
        public String getToken() { return "example/sse"; }
        public int needAudit() { return 0; }
        @Example(title = "scene.os", example = "{}")
        public Object myDoService(JSONObject param, HttpServletRequest request, HttpServletResponse response) { throw new AssertionError("Must not execute"); }
    }

    /** 方法示例在注解之后，标题相同也保留，翻译和副本均不污染原始数据。 */
    @Test
    public void mergesMethodExamplesAcrossComponentTypes() throws Exception {
        JSONObject request = new JSONObject().fluentPut("hostname", "example-host");
        java.util.List<ApiExampleVo> scenes = java.util.Arrays.asList(
                new ApiExampleVo("scene.os", "scene.help", request),
                new ApiExampleVo("scene.os", request));
        ObjectApi api = new ObjectApi() {
            @Override public java.util.List<ApiExampleVo> example() { return scenes; }
        };
        JSONArray merged = api.help().getJSONArray("example");
        Assert.assertEquals(4, merged.size());
        Assert.assertEquals("OS Health", merged.getJSONObject(2).getString("title"));
        Assert.assertEquals("Replace the hostname with a registered node.", merged.getJSONObject(2).getString("description"));
        Assert.assertEquals("", merged.getJSONObject(3).getString("description"));
        Assert.assertNotSame(request, merged.getJSONObject(2).get("example"));
        merged.getJSONObject(2).getJSONObject("example").put("hostname", "edited");
        Assert.assertEquals("example-host", request.getString("hostname"));
        Assert.assertEquals("example-host", merged.getJSONObject(3).getJSONObject("example").getString("hostname"));
        Assert.assertFalse(merged.toJSONString().contains("$ref"));
        Assert.assertEquals(3, new RawApi() {
            @Override public java.util.List<ApiExampleVo> example() { return scenes; }
        }.help().getJSONArray("example").size());
        Assert.assertEquals(3, new SseApi() {
            @Override public java.util.List<ApiExampleVo> example() { return scenes; }
        }.help().getJSONArray("example").size());
        JSONArray translated = ApiExampleUtil.getExamples(Samples.class.getMethod("empty"), scenes, key -> "translated:" + key);
        Assert.assertEquals("translated:scene.os", translated.getJSONObject(0).getString("title"));
        Assert.assertEquals(request, translated.getJSONObject(0).get("example"));
    }

    /** 空方法列表兼容 null；方法独立声明及共享嵌套对象均可完整序列化。 */
    @Test
    public void supportsMethodOnlyAndSharedObjects() throws Exception {
        java.lang.reflect.Method method = Samples.class.getMethod("empty");
        Assert.assertTrue(ApiExampleUtil.getExamples(method, (java.util.List<ApiExampleVo>) null).isEmpty());
        JSONObject nested = new JSONObject().fluentPut("value", 1);
        JSONObject request = new JSONObject().fluentPut("first", nested).fluentPut("second", nested);
        JSONArray examples = ApiExampleUtil.getExamples(method, java.util.Collections.singletonList(new ApiExampleVo("scene.os", request)));
        Assert.assertEquals(1, examples.size());
        Assert.assertFalse(examples.toJSONString().contains("$ref"));
        Assert.assertEquals(nested, examples.getJSONObject(0).getJSONObject("example").getJSONObject("second"));
    }

    /** 方法中的非法场景与注解走同一框架异常，不能被静默丢弃。 */
    @Test
    public void rejectsInvalidMethodExamples() throws Exception {
        for (ApiExampleVo value : new ApiExampleVo[]{null, new ApiExampleVo(" ", new JSONObject()),
                new ApiExampleVo("scene.os", "string"), new ApiExampleVo("scene.os", null)}) {
            Assert.assertThrows(ApiExampleInvalidException.class, () ->
                    ApiExampleUtil.getExamples(Samples.class.getMethod("empty"), java.util.Collections.singletonList(value)));
        }
    }

    /** MCP 必须消费合并结果，方法场景进入模型说明及完整调用示例。 */
    @Test
    public void publishesMergedMethodExamplesToMcp() {
        ObjectApi component = new ObjectApi() {
            @Override public java.util.List<ApiExampleVo> example() {
                return java.util.Collections.singletonList(new ApiExampleVo("scene.help", new JSONObject().fluentPut("hostname", "method-host")));
            }
        };
        String handler = component.getClass().getName();
        PrivateApiComponentFactory.getComponentMap().put(handler, component);
        try {
            ApiVo api = new ApiVo();
            api.setHandler(handler);
            api.setToken("example/health/get");
            api.setName("Health");
            JSONObject tool = McpToolMetadataBuilder.buildTool(api);
            Assert.assertTrue(tool.getString("description").contains("method-host"));
            JSONArray examples = McpToolMetadataBuilder.getExamples(api);
            Assert.assertEquals(3, examples.size());
            JSONArray calls = McpToolMetadataBuilder.getCallToolExamples(tool.getString("name"), examples);
            Assert.assertEquals("method-host", calls.getJSONObject(2).getJSONObject("example")
                    .getJSONObject("params").getJSONObject("arguments").getString("hostname"));
            Assert.assertFalse(tool.getJSONObject("_meta").containsKey("com.neatlogic/examples"));
        } finally {
            PrivateApiComponentFactory.getComponentMap().remove(handler);
        }
    }

    /** 无服务注解时，帮助组装仍读取方法声明，代理不影响合并。 */
    @Test
    public void supportsMethodOnlyHelpAndProxy() {
        ObjectApi component = new ObjectApi() {
            @Override public Object myDoService(JSONObject param) { throw new AssertionError("Must not execute"); }
            @Override public java.util.List<ApiExampleVo> example() {
                return java.util.Collections.singletonList(new ApiExampleVo("scene.os", new JSONArray()));
            }
        };
        Assert.assertEquals(1, component.help().getJSONArray("example").size());
        ProxyFactory factory = new ProxyFactory(component);
        factory.setProxyTargetClass(true);
        Assert.assertEquals(1, ((ObjectApi) factory.getProxy()).help().getJSONArray("example").size());
    }

    /** 边界输入用于区分空列表、数组内容和非法声明。 */
    public static class Samples {
        public void empty() { }
        @Example(title = "scene.os", example = "[{}]")
        public void array() { }
        @Example(title = "scene.os", example = "{bad")
        public void invalid() { }
        @Example(title = "scene.os", example = "null")
        public void scalar() { }
    }
}
