package neatlogic.framework.datawarehouse.service;

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
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;
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
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/** 使用真实公共服务、DTO 和原 Mapper XML 验证查询兼容性，代理 Mapper 不连接业务数据库。 */
public class DataSourceServiceQueryTest {
    private DataSourceServiceImpl service;
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
        service = new DataSourceServiceImpl();
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
        inject(service, "dataSourceMapper", sourceMapper);
        inject(service, "dataSourceDataMapper", dataMapper);
    }

    /** 列表原有筛选、分页和 defaultValue 回显保持不变，结果不二次包装。 */
    @Test
    public void shouldPreserveListFiltersPaginationAndSelectedValues() {
        JSONObject params = JSON.parseObject("{\"datasourceType\":\"datawarehouse\",\"keyword\":\"job\",\"moduleId\":\"autoexec\",\"isActive\":1,\"currentPage\":2,\"pageSize\":10,\"idList\":[999],\"defaultValue\":[\"10\",20]}");
        JSONObject result = service.searchDataSource(JSON.toJavaObject(params, DataSourceVo.class));
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
        service.searchDataSource(JSON.toJavaObject(JSON.parseObject("{\"idList\":[10]}"), DataSourceVo.class));
        assertEquals(Collections.singletonList(10L), listQuery.getIdList());
        assertNull(listQuery.getIsActive());
    }


    /** 空列表保留原分页结构，且不额外查询总数。 */
    @Test
    public void shouldPreserveEmptyListResponse() {
        datasourceList = new ArrayList<>();
        JSONObject result = service.searchDataSource(JSON.toJavaObject(new JSONObject(), DataSourceVo.class));
        assertEquals(0, result.getIntValue("rowNum"));
        assertEquals(0, result.getIntValue("pageCount"));
        assertTrue(result.getJSONArray("tbodyList").isEmpty());
        assertEquals(0, listCountQueryCount);
    }


    /** 详情保持 ID 参数和缺失数据源语义，取数仍按原规则抛不存在异常。 */
    @Test
    public void shouldPreserveDetailAndMissingSourceSemantics() {
        assertSame(datasource, service.getDataSourceById(request().getLong("id")));
        assertEquals(Long.valueOf(10), detailId);
        datasource = null;
        assertNull(service.getDataSourceById(request().getLong("id")));
        assertThrows(DataSourceIsNotFoundException.class, () -> service.searchDataSourceData(JSON.toJavaObject(request(), DataSourceDataVo.class)));
    }


    /** 取数响应保留业务列别名、系统列、分页元数据和行中的真实值。 */
    @Test
    public void shouldPreserveTableShapeAndRawRows() {
        JSONObject result = service.searchDataSourceData(JSON.toJavaObject(request(), DataSourceDataVo.class));
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
            service.searchDataSourceData(JSON.toJavaObject(params, DataSourceDataVo.class));
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
        JSONObject result = service.searchDataSourceData(JSON.toJavaObject(request(), DataSourceDataVo.class));
        assertEquals(0, result.getIntValue("rowNum"));
        assertEquals(4, result.getJSONArray("theadList").size());
        assertTrue(result.getJSONArray("tbodyList").isEmpty());
        datasource.setFieldList(Collections.emptyList());
        dataQuery = null;
        assertTrue(service.searchDataSourceData(JSON.toJavaObject(request(), DataSourceDataVo.class)).isEmpty());
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
                    JSONObject expected = bundle.getJSONObject("nfds")
                            .getJSONObject("datasourceserviceimpl").getJSONObject("header");
                    JSONArray headers = service.searchDataSourceData(JSON.toJavaObject(request(), DataSourceDataVo.class)).getJSONArray("theadList");
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


    /** Mapper 返回 null 列表时保持原空值形态，不触发额外 count 查询。 */
    @Test
    public void shouldPreserveNullListWithoutCounting() {
        datasourceList = null;
        JSONObject result = service.searchDataSource(new DataSourceVo());
        assertTrue(result.containsKey("tbodyList"));
        assertNull(result.get("tbodyList"));
        assertEquals(0, result.getIntValue("rowNum"));
        assertEquals(0, listCountQueryCount);
    }

    /** 字段列表为 null 时也保留原空对象语义，不调用数据表 Mapper。 */
    @Test
    public void shouldSkipDataQueriesWhenFieldListIsNull() {
        datasource.setFieldList(null);
        assertTrue(service.searchDataSourceData(new DataSourceDataVo(10L)).isEmpty());
        assertNull(dataQuery);
    }

    /** 用原 MyBatis XML 验证服务传入 DTO 的 limit 三态、排序顺序和过期条件。 */
    @Test
    public void shouldPreserveExistingDataSqlBranches() throws Exception {
        Configuration configuration = mapperConfiguration(DataWarehouseDataSourceDataMapper.class);
        for (Integer limit : Arrays.asList(null, 0, 5)) {
            // 仅固定测试表名，使用真实 DTO 其余 getter 和服务补齐字段、统计的流程。
            DataSourceDataVo request = new DataSourceDataVo(10L) {
                /** 测试表名不依赖线程租户，其他 DTO 行为保持原样。 */
                @Override
                public String getTableName() {
                    return "neatlogic_test_data.`datasource_10`";
                }
            };
            request.setCurrentPage(3);
            request.setPageSize(10);
            request.setLimit(limit);
            request.setSortList(JSON.parseArray("[{\"fieldId\":102,\"type\":\"DESC\"},{\"fieldId\":101,\"type\":\"ASC\"}]",
                    neatlogic.framework.datawarehouse.dto.DataSourceFieldSortVo.class));
            service.searchDataSourceData(request);
            assertSame(request, dataQuery);
            BoundSql bound = configuration.getMappedStatement(DataWarehouseDataSourceDataMapper.class.getName() + ".searchDataSourceData")
                    .getBoundSql(dataQuery);
            String sql = bound.getSql().replaceAll("\\s+", " ").trim();
            assertTrue(sql.contains("`101` as `field_101`"));
            assertTrue(sql.contains("`102` as `field_102`"));
            assertTrue(sql.contains("order by `field_102` DESC , `field_101` ASC"));
            assertTrue(sql.contains("expire_time is null or expire_time >= now()"));
            List<String> parameters = bound.getParameterMappings().stream().map(item -> item.getProperty()).collect(Collectors.toList());
            if (limit == null) {
                assertTrue(sql.endsWith("limit ?, ?"));
                assertEquals(Arrays.asList("startNum", "pageSize"), parameters);
            } else if (limit == 0) {
                assertFalse(sql.contains("limit"));
                assertTrue(parameters.isEmpty());
            } else {
                assertTrue(sql.endsWith("limit ?"));
                assertEquals(Collections.singletonList("limit"), parameters);
            }
            request.setIsExpired(1);
            service.searchDataSourceData(request);
            String expiredSql = configuration.getMappedStatement(DataWarehouseDataSourceDataMapper.class.getName() + ".searchDataSourceData")
                    .getBoundSql(dataQuery).getSql().replaceAll("\\s+", " ");
            String countSql = configuration.getMappedStatement(DataWarehouseDataSourceDataMapper.class.getName() + ".searchDataSourceDataCount")
                    .getBoundSql(dataQuery).getSql().replaceAll("\\s+", " ");
            assertTrue(expiredSql.contains("expire_time <= now()"));
            assertTrue(countSql.contains("expire_time <= now()"));
            assertFalse(expiredSql.contains("expire_time is null"));
            assertFalse(countSql.contains("limit"));
        }
    }

    /** 原列表 XML 继续使用过滤及绑定 ID，defaultValue 优先结果来自公共服务。 */
    @Test
    public void shouldPreserveExistingListSqlAndSelectedIds() throws Exception {
        Configuration configuration = mapperConfiguration(DataWarehouseDataSourceMapper.class);
        DataSourceVo request = JSON.toJavaObject(JSON.parseObject("{\"keyword\":\"job\",\"moduleId\":\"autoexec\",\"isActive\":1,\"currentPage\":2,\"pageSize\":10,\"idList\":[999],\"defaultValue\":[\"10\",20]}"), DataSourceVo.class);
        service.searchDataSource(request);
        assertSame(request, listQuery);
        assertEquals(Arrays.asList(10L, 20L), listQuery.getIdList());
        BoundSql bound = configuration.getMappedStatement(DataWarehouseDataSourceMapper.class.getName() + ".searchDataSource")
                .getBoundSql(listQuery);
        String sql = bound.getSql().replaceAll("\\s+", " ").trim();
        assertTrue(sql.contains("module_id = ?"));
        assertTrue(sql.contains("is_active = ?"));
        assertTrue(sql.contains("and id in"));
        assertTrue(sql.endsWith("limit ?, ?"));
        assertFalse(sql.contains("autoexec"));
        List<String> parameters = bound.getParameterMappings().stream().map(item -> item.getProperty()).collect(Collectors.toList());
        List<Object> selectedIds = bound.getParameterMappings().stream()
                .filter(item -> item.getProperty().startsWith("__frch_item_"))
                .map(item -> bound.getAdditionalParameter(item.getProperty())).collect(Collectors.toList());
        assertEquals(Arrays.asList(10L, 20L), selectedIds);
        assertEquals(Arrays.asList("startNum", "pageSize"), parameters.subList(parameters.size() - 2, parameters.size()));
    }

    /** 注册原类型别名后解析完整 Mapper，验证 MyBatis 实际生成 SQL，不改写查询片段。 */
    private Configuration mapperConfiguration(Class<?> mapper) throws Exception {
        Configuration configuration = new Configuration();
        configuration.getTypeAliasRegistry().registerAlias("CompressHandler", neatlogic.framework.dao.plugin.CompressHandler.class);
        configuration.getTypeAliasRegistry().registerAlias("Md5Handler", neatlogic.framework.dao.plugin.Md5Handler.class);
        String resource = mapper.getName().replace('.', '/') + ".xml";
        try (InputStream stream = mapper.getClassLoader().getResourceAsStream(resource)) {
            assertNotNull("原 Mapper XML 应可加载", stream);
            new XMLMapperBuilder(stream, configuration, resource, configuration.getSqlFragments()).parse();
        }
        return configuration;
    }

    /** 生成两个旧接口各自使用的 ID 参数，不引入新的统一别名。 */
    private JSONObject request() {
        return JSON.parseObject("{\"id\":10,\"dataSourceId\":10}");
    }

    /** 通过反射注入 Mapper 代理，保持生产服务的字段注入风格。 */
    private void inject(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}
