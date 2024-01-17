/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.store.mysql;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.common.util.TenantUtil;
import neatlogic.framework.dao.mapper.DatasourceMapper;
import neatlogic.framework.dto.DatasourceVo;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RootComponent
public class DatasourceManager {
    @Resource
    private DatasourceMapper datasourceMapper;

    private static NeatLogicRoutingDataSource datasource;

    // 数据库厂商，mysql、oceanbase、tidb
    private static String databaseId;

    public static void setDatabaseId(String _databaseId) {
        if (databaseId == null) {
            databaseId = _databaseId;
        }
    }

    public static String getDatabaseId() {
        return databaseId;
    }

    @Resource
    public void setDataSource(NeatLogicRoutingDataSource _datasource) {
        datasource = _datasource;
    }


    @Resource(name = "dataSourceMaster")
    private NeatLogicBasicDataSource masterDatasource;

    private static final Map<Object, Object> datasourceMap = new HashMap<>();

    public static NeatLogicBasicDataSource getDatasource() {
        return (NeatLogicBasicDataSource) (datasourceMap.get(TenantContext.get().getTenantUuid()));
    }

    public static NeatLogicBasicDataSource getDatasource(String tenantUuid) {
        return (NeatLogicBasicDataSource) (datasourceMap.get(tenantUuid));
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
                NeatLogicBasicDataSource tenantDatasource = getDataSource(datasourceVo, false);
                datasourceMap.put(datasourceVo.getTenantUuid(), tenantDatasource);
                //一个数据源足够了，直接增加前缀来查data库
                // 创建OLAP库
                NeatLogicBasicDataSource tenantDataDatasource = getDataSource(datasourceVo, true);
                datasourceMap.put(datasourceVo.getTenantUuid() + "_DATA", tenantDataDatasource);
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

    public static void addDynamicDataSource(String tenantUuid, NeatLogicBasicDataSource neatlogicBasicDataSource) {
        datasourceMap.put(tenantUuid, neatlogicBasicDataSource);
        datasource.setTargetDataSources(datasourceMap);
        if (datasourceMap.containsKey("master")) {
            datasource.setDefaultTargetDataSource(datasourceMap.get("master"));
        }
        datasource.afterPropertiesSet();
    }

    public static NeatLogicBasicDataSource getDataSource(DatasourceVo datasourceVo, Boolean isDataDB) {
        NeatLogicBasicDataSource tenantDatasource = new NeatLogicBasicDataSource();
        String url = datasourceVo.getUrl();
        url = url.replace("{host}", datasourceVo.getHost());
        url = url.replace("{port}", datasourceVo.getPort().toString());
        url = url.replace("{dbname}", "neatlogic_" + datasourceVo.getTenantUuid() + (isDataDB ? "_data" : StringUtils.EMPTY));
        //tenantDatasource.setUrl(url);
        tenantDatasource.setJdbcUrl(url);
        tenantDatasource.setDriverClassName(datasourceVo.getDriver());
        tenantDatasource.setUsername(datasourceVo.getUsername());
        tenantDatasource.setPassword(datasourceVo.getPasswordPlain());
        tenantDatasource.setPoolName("HikariCP_"+ (isDataDB ? "DATA_" : StringUtils.EMPTY) + datasourceVo.getTenantUuid());
        setDataSourceConfig(tenantDatasource);
        return tenantDatasource;
    }

    public static void setDataSourceConfig(NeatLogicBasicDataSource dataSource){
        dataSource.setMaximumPoolSize(20);
        //以下是针对Mysql的参数优化
        //This sets the number of prepared statements that the MySQL driver will cache per connection. The default is a conservative 25. We recommend setting this to between 250-500.
        dataSource.addDataSourceProperty("prepStmtCacheSize", 250);
        //This is the maximum length of a prepared SQL statement that the driver will cache. The MySQL default is 256. In our experience, especially with ORM frameworks like Hibernate, this default is well below the threshold of generated statement lengths. Our recommended setting is 2048.
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        //Neither of the above parameters have any effect if the cache is in fact disabled, as it is by default. You must set this parameter to true.
        dataSource.addDataSourceProperty("cachePrepStmts", true);
        //Newer versions of MySQL support server-side prepared statements, this can provide a substantial performance boost. Set this property to true.
        dataSource.addDataSourceProperty("useServerPrepStmts", true);
        dataSource.addDataSourceProperty("useLocalSessionState", true);
        dataSource.addDataSourceProperty("rewriteBatchedStatements", true);
        dataSource.addDataSourceProperty("cacheResultSetMetadata", true);
        dataSource.addDataSourceProperty("cacheServerConfiguration", true);
        dataSource.addDataSourceProperty("elideSetAutoCommits", true);
        dataSource.addDataSourceProperty("maintainTimeStats", false);
        dataSource.setConnectionTimeout(5000);
    }
}
