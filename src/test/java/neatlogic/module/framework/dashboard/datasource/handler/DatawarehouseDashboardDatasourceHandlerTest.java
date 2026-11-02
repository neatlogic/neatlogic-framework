package neatlogic.module.framework.dashboard.datasource.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.datawarehouse.dto.DataSourceDataVo;
import neatlogic.framework.datawarehouse.dto.DataSourceFieldVo;
import neatlogic.framework.datawarehouse.dto.DataSourceVo;
import neatlogic.framework.datawarehouse.exceptions.DataSourceIsNotFoundException;
import neatlogic.framework.datawarehouse.service.DataSourceService;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/** 保留真实 DTO 的最小字段投影，并通过服务代理验证 Handler 只做转换与委托。 */
public class DatawarehouseDashboardDatasourceHandlerTest {
    private DatawarehouseDashboardDatasourceHandler handler;
    private DataSourceVo datasource;
    private DataSourceVo listQuery;
    private DataSourceDataVo dataQuery;
    private Long detailId;
    private JSONObject listResult;
    private JSONObject dataResult;
    private RuntimeException queryFailure;

    /** 初始化原字段样例，并捕获 Handler 传给公共服务的 DTO。 */
    @Before
    public void initialize() throws Exception {
        handler = new DatawarehouseDashboardDatasourceHandler();
        datasource = new DataSourceVo();
        datasource.setId(10L);
        datasource.setName("job_duration");
        datasource.setLabel("子作业耗时");
        DataSourceFieldVo nameField = new DataSourceFieldVo();
        nameField.setId(101L);
        nameField.setName("jobName");
        nameField.setLabel("作业名");
        nameField.setType("text");
        DataSourceFieldVo valueField = new DataSourceFieldVo();
        valueField.setId(102L);
        valueField.setName("duration");
        valueField.setLabel("耗时");
        valueField.setType("number");
        datasource.setFieldList(Arrays.asList(nameField, valueField));
        listResult = new JSONObject().fluentPut("tbodyList", new JSONArray()).fluentPut("rowNum", 0);
        dataResult = new JSONObject().fluentPut("theadList", new JSONArray()).fluentPut("tbodyList", new JSONArray());
        DataSourceService service = (DataSourceService) Proxy.newProxyInstance(
                DataSourceService.class.getClassLoader(), new Class<?>[]{DataSourceService.class},
                (proxy, method, args) -> {
                    if (queryFailure != null) {
                        throw queryFailure;
                    }
                    switch (method.getName()) {
                        case "getDataSourceById":
                            detailId = (Long) args[0];
                            return datasource;
                        case "searchDataSource":
                            listQuery = (DataSourceVo) args[0];
                            return listResult;
                        case "searchDataSourceData":
                            dataQuery = (DataSourceDataVo) args[0];
                            return dataResult;
                        default:
                            throw new AssertionError("非预期公共服务调用：" + method.getName());
                    }
                });
        inject(handler, "dataSourceService", service);
    }

    /** 详情保持 ID 参数和缺失数据源语义，取数仍按原规则抛不存在异常。 */
    @Test
    public void shouldPreserveDetailAndMissingSourceSemantics() {
        assertNotNull(handler.getDatasource(request()));
        assertEquals(Long.valueOf(10), detailId);
        datasource = null;
        assertNull(handler.getDatasource(request()));
        queryFailure = new DataSourceIsNotFoundException(10L);
        assertSame(queryFailure, assertThrows(DataSourceIsNotFoundException.class, () -> handler.searchDatasourceData(request())));
    }


    /** 详情只暴露六个字段属性，使用真实 JSON 容器并保留原字段顺序、配置引用和源对象。 */
    @Test
    public void shouldReturnOnlyMinimalJsonFieldsWithoutChangingSource() throws Exception {
        List<DataSourceFieldVo> originalFields = datasource.getFieldList();
        DataSourceFieldVo nameField = originalFields.get(0);
        DataSourceFieldVo valueField = originalFields.get(1);
        JSONObject config = JSON.parseObject("{\"url\":\"/field/options\",\"params\":{\"id\":10},\"dataList\":[{\"value\":\"a\",\"text\":\"A\"}]}");
        nameField.setId(301L);
        nameField.setDataSourceId(10L);
        nameField.setIsKey(1);
        nameField.setIsCondition(1);
        nameField.setInputType("select");
        nameField.setConfig(config);
        nameField.setConfigStr("{\"ignored\":true}");
        nameField.setTypeText("文本");
        nameField.setInputTypeText("下拉框");
        JSONObject result = handler.getDatasource(request());
        assertEquals(Collections.singleton("fieldList"), result.keySet());
        assertEquals(JSONArray.class, result.get("fieldList").getClass());
        JSONArray fields = (JSONArray) result.get("fieldList");
        assertEquals(2, fields.size());
        Set<String> expectedKeys = new HashSet<>(Arrays.asList("id", "label", "type", "isCondition", "inputType", "config"));
        for (Object field : fields) {
            assertEquals(JSONObject.class, field.getClass());
            assertEquals(expectedKeys, ((JSONObject) field).keySet());
        }
        JSONObject first = (JSONObject) fields.get(0);
        JSONObject second = (JSONObject) fields.get(1);
        assertEquals(Arrays.asList(301L, 102L), Arrays.asList(first.getLong("id"), second.getLong("id")));
        assertEquals("作业名", first.getString("label"));
        assertEquals("text", first.getString("type"));
        assertEquals(Integer.valueOf(1), first.getInteger("isCondition"));
        assertEquals("select", first.getString("inputType"));
        assertSame(config, first.get("config"));
        assertEquals("number", second.getString("type"));
        assertNull(second.get("config"));
        // 序列化沿用现有空值策略，但任何阶段都不能泄露 DTO 的额外字段。
        JSONObject serialized = JSON.parseObject(JSON.toJSONString(result));
        assertEquals(Collections.singleton("fieldList"), serialized.keySet());
        for (Object field : serialized.getJSONArray("fieldList")) {
            assertTrue(expectedKeys.containsAll(((JSONObject) field).keySet()));
        }
        assertSame(originalFields, datasource.getFieldList());
        assertSame(nameField, datasource.getFieldList().get(0));
        assertSame(valueField, datasource.getFieldList().get(1));
        assertEquals(Long.valueOf(10), datasource.getId());
        assertEquals("job_duration", datasource.getName());
        assertEquals("子作业耗时", datasource.getLabel());
        assertEquals(Long.valueOf(301), nameField.getId());
        assertEquals("jobName", nameField.getName());
        assertEquals("作业名", nameField.getLabel());
        assertEquals(Long.valueOf(10), nameField.getDataSourceId());
        assertEquals(Integer.valueOf(1), nameField.getIsKey());
        assertEquals(Integer.valueOf(1), nameField.getIsCondition());
        assertEquals("text", nameField.getType());
        assertEquals("select", nameField.getInputType());
        assertSame(config, nameField.getConfig());
        assertEquals("{\"ignored\":true}", fieldValue(nameField, "configStr"));
        assertEquals("文本", nameField.getTypeText());
        assertEquals("下拉框", nameField.getInputTypeText());
    }


    /** 使用 getter 的标签回退和 text 默认值，不把默认值写回源字段。 */
    @Test
    public void shouldPreserveDetailGetterDefaults() throws Exception {
        for (String emptyValue : Arrays.asList(null, "", " \t")) {
            DataSourceFieldVo field = new DataSourceFieldVo();
            field.setId(103L);
            field.setName("fallbackName");
            field.setLabel(emptyValue);
            field.setType(emptyValue);
            field.setInputType(emptyValue);
            datasource.setFieldList(Collections.singletonList(field));
            JSONObject result = handler.getDatasource(request()).getJSONArray("fieldList").getJSONObject(0);
            assertEquals("fallbackName", result.getString("label"));
            assertEquals("text", result.getString("type"));
            assertEquals("text", result.getString("inputType"));
            assertEquals(Integer.valueOf(0), result.getInteger("isCondition"));
            assertTrue(result.containsKey("config"));
            assertNull(result.get("config"));
            assertEquals(emptyValue, fieldValue(field, "label"));
            assertEquals(emptyValue, fieldValue(field, "type"));
            assertEquals(emptyValue, fieldValue(field, "inputType"));
        }
    }


    /** configStr 沿用 getter 的延迟解析和无效配置处理，不重新包装或改写配置对象。 */
    @Test
    public void shouldPreserveConfigStringParsingAndReference() throws Exception {
        DataSourceFieldVo field = datasource.getFieldList().get(0);
        String configStr = "{\"multiple\":true,\"params\":{\"moduleId\":\"autoexec\"},\"dataList\":[{\"value\":1}]}";
        field.setConfigStr(configStr);
        JSONObject result = handler.getDatasource(request()).getJSONArray("fieldList").getJSONObject(0);
        assertEquals(JSONObject.class, result.get("config").getClass());
        assertEquals(JSON.parseObject(configStr), result.get("config"));
        assertSame(field.getConfig(), result.get("config"));
        assertEquals(configStr, fieldValue(field, "configStr"));
        for (String invalidConfig : Arrays.asList(null, "", "not-json")) {
            field.setConfig(null);
            field.setConfigStr(invalidConfig);
            JSONObject invalidResult = handler.getDatasource(request()).getJSONArray("fieldList").getJSONObject(0);
            assertTrue(invalidResult.containsKey("config"));
            assertNull(invalidResult.get("config"));
            assertEquals(invalidConfig, fieldValue(field, "configStr"));
        }
    }


    /** null 或空字段列表统一输出 JSON 空数组，且不修改 Mapper 返回的字段列表。 */
    @Test
    public void shouldReturnEmptyJsonArrayForMissingFields() {
        for (List<DataSourceFieldVo> fields : Arrays.asList(null, Collections.<DataSourceFieldVo>emptyList())) {
            datasource.setFieldList(fields);
            JSONObject result = handler.getDatasource(request());
            assertEquals(Collections.singleton("fieldList"), result.keySet());
            assertEquals(JSONArray.class, result.get("fieldList").getClass());
            assertTrue(((JSONArray) result.get("fieldList")).isEmpty());
            assertSame(fields, datasource.getFieldList());
        }
    }


    /** 列表只完成 JSON 到 DTO 的转换，回显优先级及分页由公共服务处理，返回对象原样透传。 */
    @Test
    public void shouldDelegateListQueryWithoutChangingServiceResult() {
        JSONObject params = JSON.parseObject("{\"datasourceType\":\"datawarehouse\",\"keyword\":\"job\",\"moduleId\":\"autoexec\",\"currentPage\":2,\"pageSize\":10,\"idList\":[999],\"defaultValue\":[\"10\",20]}");
        assertSame(listResult, handler.listDatasource(params));
        assertEquals("job", listQuery.getKeyword());
        assertEquals("autoexec", listQuery.getModuleId());
        assertEquals(Collections.singletonList(999L), listQuery.getIdList());
        assertEquals(params.getJSONArray("defaultValue"), listQuery.getDefaultValue());
        assertEquals(Integer.valueOf(2), listQuery.getCurrentPage());
        assertEquals(Integer.valueOf(10), listQuery.getPageSize());
    }

    /** 动态取数的过滤、排序、过期及 limit 三态完整交给服务，Handler 不重新包装响应。 */
    @Test
    public void shouldDelegateDataQueryWithoutChangingServiceResult() {
        for (Integer limit : Arrays.asList(null, 0, 5)) {
            JSONObject params = request();
            params.put("currentPage", 3);
            params.put("pageSize", 10);
            params.put("isExpired", 1);
            params.put("sortList", JSON.parseArray("[{\"fieldId\":102,\"type\":\"DESC\"},{\"fieldId\":101,\"type\":\"ASC\"}]"));
            params.put("conditionList", JSON.parseArray("[{\"id\":101,\"inputType\":\"text\",\"value\":\"应用一\"}]"));
            if (limit != null) {
                params.put("limit", limit);
            }
            assertSame(dataResult, handler.searchDatasourceData(params));
            assertEquals(Long.valueOf(10), dataQuery.getDataSourceId());
            assertEquals(limit, dataQuery.getLimit());
            assertEquals(Integer.valueOf(3), dataQuery.getCurrentPage());
            assertEquals(Integer.valueOf(10), dataQuery.getPageSize());
            assertEquals(Integer.valueOf(1), dataQuery.getIsExpired());
            assertEquals(Long.valueOf(102), dataQuery.getSortList().get(0).getFieldId());
            assertEquals("DESC", dataQuery.getSortList().get(0).getType());
            assertEquals(Long.valueOf(101), dataQuery.getSortList().get(1).getFieldId());
            assertEquals("ASC", dataQuery.getSortList().get(1).getType());
            assertEquals("应用一", dataQuery.getConditionList().get(0).getValue());
            assertNull(dataQuery.getFieldList());
        }
    }

    /** 查询失败由接口框架处理，Handler 保留服务异常实例，不吞错或更换业务异常类型。 */
    @Test
    public void shouldPropagateServiceQueryFailures() {
        queryFailure = new DataSourceIsNotFoundException(10L);
        assertSame(queryFailure, assertThrows(DataSourceIsNotFoundException.class, () -> handler.getDatasource(request())));
        assertSame(queryFailure, assertThrows(DataSourceIsNotFoundException.class, () -> handler.listDatasource(request())));
        assertSame(queryFailure, assertThrows(DataSourceIsNotFoundException.class, () -> handler.searchDatasourceData(request())));
    }

    /** 生成两个旧接口各自使用的 ID 参数，不引入新的统一别名。 */
    private JSONObject request() {
        return JSON.parseObject("{\"id\":10,\"dataSourceId\":10}");
    }

    /** 直接读取原始属性验证映射未回写默认值，避免整体序列化 DTO 触发额外 getter。 */
    private Object fieldValue(Object target, String name) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(target);
    }

    /** 通过反射注入服务代理，保持生产处理器的字段注入风格。 */
    private void inject(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}
