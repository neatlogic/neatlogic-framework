package neatlogic.module.framework.dashboard.datasource.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.datawarehouse.dao.mapper.DataWarehouseDataSourceDataMapper;
import neatlogic.framework.datawarehouse.dao.mapper.DataWarehouseDataSourceMapper;
import neatlogic.framework.datawarehouse.dto.DataSourceDataVo;
import neatlogic.framework.datawarehouse.dto.DataSourceFieldVo;
import neatlogic.framework.datawarehouse.dto.DataSourceVo;
import neatlogic.framework.datawarehouse.exceptions.DataSourceIsNotFoundException;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.junit.Assert.*;

/** 使用真实 DTO 和处理器、代理 Mapper 验证旧数据仓库查询契约，无需业务数据库。 */
public class DatawarehouseDashboardDatasourceHandlerTest {
    private DatawarehouseDashboardDatasourceHandler handler;
    private DataSourceVo datasource;
    private List<DataSourceVo> datasourceList;
    private List<HashMap<String, Object>> rows;
    private DataSourceVo listQuery;
    private DataSourceDataVo dataQuery;
    private Long detailId;
    private int listCountQueryCount;

    /** 初始化字段与结果样例，捕获实际传给 Mapper 的查询对象。 */
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
        datasourceList = Collections.singletonList(datasource);
        HashMap<String, Object> row = new HashMap<>();
        row.put("id", 1001L);
        row.put("field_101", "应用一");
        row.put("field_102", 12.5);
        rows = Collections.singletonList(row);
        DataWarehouseDataSourceMapper sourceMapper = (DataWarehouseDataSourceMapper) Proxy.newProxyInstance(
                DataWarehouseDataSourceMapper.class.getClassLoader(), new Class<?>[]{DataWarehouseDataSourceMapper.class},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "searchDataSource":
                            listQuery = (DataSourceVo) args[0];
                            return datasourceList;
                        case "searchDataSourceCount":
                            listCountQueryCount++;
                            return 41;
                        case "getDataSourceById":
                            detailId = (Long) args[0];
                            return datasource;
                        default:
                            throw new AssertionError("非预期 Mapper 调用：" + method.getName());
                    }
                });
        DataWarehouseDataSourceDataMapper dataMapper = (DataWarehouseDataSourceDataMapper) Proxy.newProxyInstance(
                DataWarehouseDataSourceDataMapper.class.getClassLoader(), new Class<?>[]{DataWarehouseDataSourceDataMapper.class},
                (proxy, method, args) -> {
                    dataQuery = (DataSourceDataVo) args[0];
                    if ("searchDataSourceDataCount".equals(method.getName())) {
                        return rows.size();
                    }
                    if ("searchDataSourceData".equals(method.getName())) {
                        return rows;
                    }
                    throw new AssertionError("非预期 Mapper 调用：" + method.getName());
                });
        inject(handler, "dataSourceMapper", sourceMapper);
        inject(handler, "dataSourceDataMapper", dataMapper);
    }

    /** 列表原有筛选、分页和 defaultValue 回显保持不变，结果不二次包装。 */
    @Test
    public void shouldPreserveListFiltersPaginationAndSelectedValues() {
        JSONObject params = JSON.parseObject("{\"datasourceType\":\"datawarehouse\",\"keyword\":\"job\",\"moduleId\":\"autoexec\",\"isActive\":1,\"currentPage\":2,\"pageSize\":10,\"idList\":[999],\"defaultValue\":[\"10\",20]}");
        JSONObject result = handler.listDatasource(params);
        assertEquals("job", listQuery.getKeyword());
        assertEquals("autoexec", listQuery.getModuleId());
        assertEquals(Integer.valueOf(1), listQuery.getIsActive());
        assertEquals(Arrays.asList(10L, 20L), listQuery.getIdList());
        assertEquals(Integer.valueOf(10), listQuery.getStartNum());
        assertEquals(2, result.getIntValue("currentPage"));
        assertEquals(10, result.getIntValue("pageSize"));
        assertEquals(41, result.getIntValue("rowNum"));
        assertEquals(5, result.getIntValue("pageCount"));
        assertSame(datasourceList, result.get("tbodyList"));
        assertTrue(result.containsKey("theadList"));
        assertFalse(result.containsKey("Return"));
    }

    /** 未提供回显参数时保留显式 ID 列表，不凭空添加启用状态过滤。 */
    @Test
    public void shouldPreserveIdListAndUnfilteredActiveState() {
        handler.listDatasource(JSON.parseObject("{\"idList\":[10]}"));
        assertEquals(Collections.singletonList(10L), listQuery.getIdList());
        assertNull(listQuery.getIsActive());
    }

    /** 空列表保留原分页结构，且不额外查询总数。 */
    @Test
    public void shouldPreserveEmptyListResponse() {
        datasourceList = new ArrayList<>();
        JSONObject result = handler.listDatasource(new JSONObject());
        assertEquals(0, result.getIntValue("rowNum"));
        assertEquals(0, result.getIntValue("pageCount"));
        assertTrue(result.getJSONArray("tbodyList").isEmpty());
        assertEquals(0, listCountQueryCount);
    }

    /** 详情保持 ID 参数和缺失数据源语义，取数仍按原规则抛不存在异常。 */
    @Test
    public void shouldPreserveDetailAndMissingSourceSemantics() {
        assertNotNull(handler.getDatasource(request()));
        assertEquals(Long.valueOf(10), detailId);
        datasource = null;
        assertNull(handler.getDatasource(request()));
        assertThrows(DataSourceIsNotFoundException.class, () -> handler.searchDatasourceData(request()));
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

    /** 取数响应保留业务列别名、系统列、分页元数据和行中的真实值。 */
    @Test
    public void shouldPreserveTableShapeAndRawRows() {
        JSONObject result = handler.searchDatasourceData(request());
        JSONArray headers = result.getJSONArray("theadList");
        assertEquals(4, headers.size());
        assertEquals("field_101", headers.getJSONObject(0).getString("key"));
        assertEquals("jobName", headers.getJSONObject(0).getString("name"));
        assertEquals("作业名", headers.getJSONObject(0).getString("title"));
        assertEquals("field_102", headers.getJSONObject(1).getString("key"));
        assertEquals("duration", headers.getJSONObject(1).getString("name"));
        assertEquals("insertTime", headers.getJSONObject(2).getString("key"));
        assertEquals("expireTime", headers.getJSONObject(3).getString("key"));
        assertEquals("time", headers.getJSONObject(2).getString("type"));
        assertFalse(headers.getJSONObject(2).containsKey("name"));
        assertSame(rows, result.get("tbodyList"));
        assertSame(datasource.getFieldList(), dataQuery.getFieldList());
        assertEquals(1, result.getIntValue("rowNum"));
        assertEquals(1, result.getIntValue("currentPage"));
        assertEquals(20, result.getIntValue("pageSize"));
        assertFalse(result.containsKey("Return"));
    }

    /** limit 缺省/0/正数原样进入 Mapper，同时保留多字段顺序、过滤及过期配置。 */
    @Test
    public void shouldForwardLimitFilteringAndSortPriority() {
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
            handler.searchDatasourceData(params);
            assertEquals(limit, dataQuery.getLimit());
            assertEquals(Integer.valueOf(20), dataQuery.getStartNum());
            assertEquals(Integer.valueOf(1), dataQuery.getIsExpired());
            assertEquals(Long.valueOf(102), dataQuery.getSortList().get(0).getFieldId());
            assertEquals("DESC", dataQuery.getSortList().get(0).getType());
            assertEquals(Long.valueOf(101), dataQuery.getSortList().get(1).getFieldId());
            assertEquals("ASC", dataQuery.getSortList().get(1).getType());
            assertEquals("应用一", dataQuery.getConditionList().get(0).getValue());
        }
    }

    /** 存在字段但无数据时仍返回表头；没有字段时沿用原接口空对象响应。 */
    @Test
    public void shouldDistinguishNoRowsFromNoFields() {
        rows = new ArrayList<>();
        JSONObject result = handler.searchDatasourceData(request());
        assertEquals(0, result.getIntValue("rowNum"));
        assertEquals(4, result.getJSONArray("theadList").size());
        assertTrue(result.getJSONArray("tbodyList").isEmpty());
        datasource.setFieldList(Collections.emptyList());
        dataQuery = null;
        assertTrue(handler.searchDatasourceData(request()).isEmpty());
        assertNull(dataQuery);
    }

    /** 仅加载 framework 的真实资源，按请求语言切换系统表头，并恢复已有线程语言。 */
    @Test
    public void shouldLocalizeSystemHeadersUsingFrameworkResources() throws Exception {
        RequestContext originalContext = RequestContext.get();
        Locale originalLocale = originalContext == null ? null : originalContext.getLocale();
        RequestContext context = originalContext == null ? RequestContext.init((RequestContext) null) : originalContext;
        try {
            for (Locale locale : Arrays.asList(Locale.CHINESE, Locale.ENGLISH, Locale.CHINESE)) {
                context.setLocale(locale);
                String language = locale.getLanguage();
                String resource = "/neatlogic/resources/framework/i18n/language_" + language + ".json";
                try (InputStream stream = getClass().getResourceAsStream(resource)) {
                    assertNotNull("framework 必须独立提供系统表头资源", stream);
                    JSONObject bundle = JSON.parseObject(new String(stream.readAllBytes(), StandardCharsets.UTF_8));
                    JSONObject expected = bundle.getJSONObject("nmfddh")
                            .getJSONObject("datawarehousedashboarddatasourcehandler").getJSONObject("header");
                    JSONArray headers = handler.searchDatasourceData(request()).getJSONArray("theadList");
                    assertEquals(expected.getString("inserttime"), headers.getJSONObject(2).getString("title"));
                    assertEquals(expected.getString("expiretime"), headers.getJSONObject(3).getString("title"));
                    assertEquals("en".equals(language) ? "Sync Time" : "同步时间", headers.getJSONObject(2).getString("title"));
                    assertEquals("en".equals(language) ? "Expiration Time" : "过期时间", headers.getJSONObject(3).getString("title"));
                }
            }
        } finally {
            if (originalContext == null) {
                context.release();
            } else {
                originalContext.setLocale(originalLocale);
            }
        }
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

    /** 通过反射注入 Mapper 代理，保持生产处理器的字段注入风格。 */
    private void inject(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}
