/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.store.mysql;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.common.util.TenantUtil;
import neatlogic.framework.dao.mapper.DatasourceMapper;
import neatlogic.framework.dto.DatasourceVo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RootComponent
public class DatasourceManager {
    @Autowired
    private DatasourceMapper datasourceMapper;

    private static CodeDriverRoutingDataSource datasource;

    @Resource
    public void setDataSource(CodeDriverRoutingDataSource _datasource) {
        datasource = _datasource;
    }


    @Resource(name = "dataSourceMaster")
    private CodeDriverBasicDataSource masterDatasource;

    private static final Map<Object, Object> datasourceMap = new HashMap<>();

    public static CodeDriverBasicDataSource getDatasource() {
        return (CodeDriverBasicDataSource) (datasourceMap.get(TenantContext.get().getTenantUuid()));
    }

    public static CodeDriverBasicDataSource getDatasource(String tenantUuid) {
        return (CodeDriverBasicDataSource) (datasourceMap.get(tenantUuid));
    }


    @PostConstruct
    public void init() {
        List<DatasourceVo> datasourceList = datasourceMapper.getAllActiveTenantDatasource();
        if (!datasourceMap.containsKey("master")) {
            datasourceMap.put("master", masterDatasource);
        }

        for (DatasourceVo datasourceVo : datasourceList) {
            if (!datasourceMap.containsKey(datasourceVo.getTenantUuid())) {
                // 创建OLTP库
                CodeDriverBasicDataSource tenantDatasource = new CodeDriverBasicDataSource();
                String url = datasourceVo.getUrl();
                url = url.replace("{host}", datasourceVo.getHost());
                url = url.replace("{port}", datasourceVo.getPort().toString());
                url = url.replace("{dbname}", "codedriver_" + datasourceVo.getDatabase());
                //tenantDatasource.setUrl(url);
                tenantDatasource.setJdbcUrl(url);
                tenantDatasource.setDriverClassName(datasourceVo.getDriver());
                tenantDatasource.setUsername(datasourceVo.getUsername());
                tenantDatasource.setPassword(datasourceVo.getPasswordPlain());
                tenantDatasource.setPoolName("HikariCP_" + datasourceVo.getTenantUuid());
                tenantDatasource.setMaximumPoolSize(20);
                //以下是针对Mysql的参数优化
                //This sets the number of prepared statements that the MySQL driver will cache per connection. The default is a conservative 25. We recommend setting this to between 250-500.
                tenantDatasource.addDataSourceProperty("prepStmtCacheSize", 250);
                //This is the maximum length of a prepared SQL statement that the driver will cache. The MySQL default is 256. In our experience, especially with ORM frameworks like Hibernate, this default is well below the threshold of generated statement lengths. Our recommended setting is 2048.
                tenantDatasource.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
                //Neither of the above parameters have any effect if the cache is in fact disabled, as it is by default. You must set this parameter to true.
                tenantDatasource.addDataSourceProperty("cachePrepStmts", true);
                //Newer versions of MySQL support server-side prepared statements, this can provide a substantial performance boost. Set this property to true.
                tenantDatasource.addDataSourceProperty("useServerPrepStmts", true);
                tenantDatasource.addDataSourceProperty("useLocalSessionState", true);
                tenantDatasource.addDataSourceProperty("rewriteBatchedStatements", true);
                tenantDatasource.addDataSourceProperty("cacheResultSetMetadata", true);
                tenantDatasource.addDataSourceProperty("cacheServerConfiguration", true);
                tenantDatasource.addDataSourceProperty("elideSetAutoCommits", true);
                tenantDatasource.addDataSourceProperty("maintainTimeStats", false);

                datasourceMap.put(datasourceVo.getTenantUuid(), tenantDatasource);


                //一个数据源足够了，直接增加前缀来查data库
                // 创建OLAP库
                CodeDriverBasicDataSource tenantDatasourceData = new CodeDriverBasicDataSource();
                String urlOlap = datasourceVo.getUrl();
                urlOlap = urlOlap.replace("{host}", datasourceVo.getHost());
                urlOlap = urlOlap.replace("{port}", datasourceVo.getPort().toString());
                urlOlap = urlOlap.replace("{dbname}", "codedriver_" + datasourceVo.getTenantUuid() + "_data");
                tenantDatasourceData.setJdbcUrl(urlOlap);
                tenantDatasourceData.setDriverClassName(datasourceVo.getDriver());
                tenantDatasourceData.setUsername(datasourceVo.getUsername());
                tenantDatasourceData.setPassword(datasourceVo.getPasswordPlain());
                tenantDatasourceData.setPoolName("HikariCP_DATA_" + datasourceVo.getTenantUuid());
                tenantDatasourceData.setMaximumPoolSize(20);
                //以下是针对Mysql的参数优化
                //This sets the number of prepared statements that the MySQL driver will cache per connection. The default is a conservative 25. We recommend setting this to between 250-500.
                tenantDatasourceData.addDataSourceProperty("prepStmtCacheSize", 250);
                //This is the maximum length of a prepared SQL statement that the driver will cache. The MySQL default is 256. In our experience, especially with ORM frameworks like Hibernate, this default is well below the threshold of generated statement lengths. Our recommended setting is 2048.
                tenantDatasourceData.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
                //Neither of the above parameters have any effect if the cache is in fact disabled, as it is by default. You must set this parameter to true.
                tenantDatasourceData.addDataSourceProperty("cachePrepStmts", true);
                //Newer versions of MySQL support server-side prepared statements, this can provide a substantial performance boost. Set this property to true.
                tenantDatasourceData.addDataSourceProperty("useServerPrepStmts", true);
                tenantDatasourceData.addDataSourceProperty("useLocalSessionState", true);
                tenantDatasourceData.addDataSourceProperty("rewriteBatchedStatements", true);
                tenantDatasourceData.addDataSourceProperty("cacheResultSetMetadata", true);
                tenantDatasourceData.addDataSourceProperty("cacheServerConfiguration", true);
                tenantDatasourceData.addDataSourceProperty("elideSetAutoCommits", true);
                tenantDatasourceData.addDataSourceProperty("maintainTimeStats", false);

                datasourceMap.put(datasourceVo.getTenantUuid() + "_DATA", tenantDatasourceData);
                TenantUtil.addTenant(datasourceVo.getTenantUuid());
            }
        }
        if (!datasourceMap.isEmpty()) {
            datasource.setTargetDataSources(datasourceMap);
            if (datasourceMap.containsKey("master")) {
                datasource.setDefaultTargetDataSource(datasourceMap.get("master"));
            }
            datasource.afterPropertiesSet();
        }
    }

    public static void addDynamicDataSource(String tenantUuid, CodeDriverBasicDataSource codeDriverBasicDataSource) {
        if (!datasourceMap.containsKey(tenantUuid)) {
            datasourceMap.put(tenantUuid, codeDriverBasicDataSource);
            datasource.setTargetDataSources(datasourceMap);
            if (datasourceMap.containsKey("master")) {
                datasource.setDefaultTargetDataSource(datasourceMap.get("master"));
            }
            datasource.afterPropertiesSet();
        }
    }

}
