/*Copyright (C) 2023  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.module.framework.datawarehouse.handler;

import neatlogic.framework.datawarehouse.core.DataSourceServiceHandlerBase;
import neatlogic.framework.datawarehouse.dao.mapper.DataWarehouseConnectionMapper;
import neatlogic.framework.datawarehouse.dao.mapper.DataWarehouseDataSourceMapper;
import neatlogic.framework.datawarehouse.dto.*;
import neatlogic.framework.datawarehouse.enums.DatabaseVersion;
import neatlogic.framework.datawarehouse.exceptions.DatabaseVersionNotFoundException;
import neatlogic.framework.datawarehouse.exceptions.ReportDataSourceSyncException;
import neatlogic.framework.datawarehouse.service.DataSourceService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Service
public class MysqlDataSourceHandler extends DataSourceServiceHandlerBase {
    static Logger logger = LoggerFactory.getLogger(MysqlDataSourceHandler.class);
    int FETCH_SIZE = 1000;
    @Resource
    private DataSource dataSource;
    @Resource
    private DataWarehouseConnectionMapper reportConnectionMapper;
    @Resource
    private DataWarehouseDataSourceMapper dataSourceMapper;
    @Resource
    DataSourceService dataSourceService;

    @Override
    public String getHandler() {
        return "mysql";
    }

    @Override
    public void mySyncData(DataSourceVo dataSourceVo, DataSourceAuditVo reportDataSourceAuditVo) {
        Connection conn = null;
        PreparedStatement queryStatement = null;
        ResultSet resultSet = null;

        try {
            List<SelectVo> selectList = getSqlFromDataSource(dataSourceVo);
            conn = getConnection(dataSourceVo);
            for (SelectVo select : selectList) {
                String sqlText = select.getSql();
                queryStatement = conn.prepareStatement(sqlText, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                queryStatement.setFetchSize(FETCH_SIZE);
                queryStatement.setFetchDirection(ResultSet.FETCH_FORWARD);
                if (dataSourceVo.getQueryTimeout() != null && dataSourceVo.getQueryTimeout() > 0) {
                    queryStatement.setQueryTimeout(dataSourceVo.getQueryTimeout());
                }

                if (CollectionUtils.isNotEmpty(select.getParamList())) {
                    for (int p = 0; p < select.getParamList().size(); p++) {
                        if (select.getParamList().get(p) instanceof String || select.getParamList().get(p) instanceof Number) {
                            queryStatement.setObject(p + 1, select.getParamList().get(p));
                        } else if (select.getParamList().get(p) instanceof String[]) {
                            // 数组参数有待处理
                            queryStatement.setObject(p + 1, ((String[]) select.getParamList().get(p))[0]);
                        } else if (select.getParamList().get(p) instanceof Number[]) {
                            // 数组参数有待处理
                            queryStatement.setObject(p + 1, ((Number[]) select.getParamList().get(p))[0]);
                        }
                    }
                }
                    /*
                      新增日志记录
                     */
                if (logger.isInfoEnabled()) {
                    logger.info("REPORT RUN SQL::" + sqlText);
                }

                resultSet = queryStatement.executeQuery();

                ResultSetMetaData metaData = resultSet.getMetaData();
                Map<String, Integer> fieldMap = new HashMap<>();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    fieldMap.put(metaData.getColumnLabel(i).toLowerCase(), i);
                }

                while (resultSet.next()) {
                    DataSourceDataVo reportDataSourceDataVo = new DataSourceDataVo(dataSourceVo.getId());
                    reportDataSourceDataVo.setExpireMinute(dataSourceVo.getExpireMinute());
                    List<DataSourceFieldVo> aggregateFieldList = new ArrayList<>();
                    List<DataSourceFieldVo> keyFieldList = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(dataSourceVo.getParamList())) {
                        for (DataSourceParamVo paramVo : dataSourceVo.getParamList()) {
                            if (fieldMap.containsKey(paramVo.getName().toLowerCase())) {
                                Object v = resultSet.getObject(fieldMap.get(paramVo.getName().toLowerCase()));
                                Long lv = null;
                                try {
                                    lv = (Long) v;
                                } catch (Exception ex) {
                                    logger.error(ex.getMessage(), ex);
                                }
                                if (lv != null) {
                                    if (paramVo.getCurrentValue() == null) {
                                        paramVo.setCurrentValue(lv);
                                    } else if (lv > paramVo.getCurrentValue()) {
                                        paramVo.setCurrentValue(lv);
                                    }
                                }
                            }
                        }
                    }
                    for (DataSourceFieldVo fieldVo : dataSourceVo.getFieldList()) {
                        if (fieldMap.containsKey(fieldVo.getName().toLowerCase())) {
                            Object v = resultSet.getObject(fieldMap.get(fieldVo.getName().toLowerCase()));
                            fieldVo.setValue(v != null ? v : "");//把所有的null值都转成空字符串
                        }
                        reportDataSourceDataVo.addField(fieldVo);
                        if (StringUtils.isNotBlank(fieldVo.getAggregate())) {
                            aggregateFieldList.add(fieldVo);
                        }
                        if (fieldVo.getIsKey().equals(1)) {
                            keyFieldList.add(fieldVo);
                        }
                    }
                    aggregateAndInsertData(aggregateFieldList, keyFieldList, reportDataSourceDataVo, reportDataSourceAuditVo);
                }
                if (CollectionUtils.isNotEmpty(dataSourceVo.getParamList())) {
                    for (DataSourceParamVo param : dataSourceVo.getParamList()) {
                        dataSourceMapper.updateDataSourceParamCurrentValue(param);
                    }
                }
            }
        } catch (SQLException | DocumentException | InstantiationException | IllegalAccessException |
                ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
            reportDataSourceAuditVo.setError(e.getMessage());
            throw new ReportDataSourceSyncException(dataSourceVo, e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (queryStatement != null) {
                    queryStatement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private Connection getConnection(DataSourceVo reportDataSourceVo) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (reportDataSourceVo.getConnectionId() != null) {
            ConnectionVo connectionVo = reportConnectionMapper.getConnectionById(reportDataSourceVo.getConnectionId());
            if (connectionVo != null) {
                DatabaseVersion version = DatabaseVersion.getVersion(connectionVo.getDatabaseVersion());
                if (version == null) {
                    throw new DatabaseVersionNotFoundException(connectionVo.getDatabaseVersion());
                }
                Class<?> clazz = Class.forName(version.getDriver());
                Driver driver = ((Driver) clazz.newInstance());
                Properties props = new Properties();
                if (StringUtils.isNoneBlank(connectionVo.getUsername())) {
                    props.put("user", connectionVo.getUsername());
                }
                if (StringUtils.isNotBlank(connectionVo.getPasswordPlain())) {
                    props.put("password", connectionVo.getPasswordPlain());
                }
                return driver.connect(connectionVo.getUrl(), props);
            }
        }
        //什么都没则返回默认连接
        return dataSource.getConnection();
    }
}
