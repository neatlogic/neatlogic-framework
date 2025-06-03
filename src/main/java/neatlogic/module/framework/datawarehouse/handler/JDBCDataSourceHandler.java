/*
 * Copyright (C) 2025  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.module.framework.datawarehouse.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.util.FileUtil;
import neatlogic.framework.datawarehouse.core.DataSourceServiceHandlerBase;
import neatlogic.framework.datawarehouse.dao.mapper.DataBaseMapper;
import neatlogic.framework.datawarehouse.dao.mapper.DataWarehouseDataSourceMapper;
import neatlogic.framework.datawarehouse.dto.*;
import neatlogic.framework.datawarehouse.exceptions.DatabaseConnectionFailedException;
import neatlogic.framework.datawarehouse.exceptions.DatabaseNotFoundException;
import neatlogic.framework.datawarehouse.exceptions.ReportDataSourceSyncException;
import neatlogic.framework.exception.file.FileNotFoundException;
import neatlogic.framework.file.dao.mapper.FileMapper;
import neatlogic.framework.file.dto.FileVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.*;

@Component
public class JDBCDataSourceHandler extends DataSourceServiceHandlerBase {
    static Logger logger = LoggerFactory.getLogger(JDBCDataSourceHandler.class);
    int FETCH_SIZE = 1000;
    @Resource
    private DataWarehouseDataSourceMapper dataSourceMapper;
    @Resource
    private DataBaseMapper dataBaseMapper;

    @Resource
    private FileMapper fileMapper;

    @Override
    public String getHandler() {
        return "jdbc";
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
                    ClassLoader classLoader = conn.getClass().getClassLoader();
                    conn.close();
                    if (classLoader instanceof URLClassLoader) {
                        ((URLClassLoader) classLoader).close();
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private Connection getConnection(DataSourceVo dataSourceVo) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        DatabaseVo dataBaseVo = dataBaseMapper.getDataBaseById(dataSourceVo.getDatabaseId());
        if (dataBaseVo == null) {
            throw new DatabaseNotFoundException(dataSourceVo.getDatabaseId());
        }
        JSONObject config = dataBaseVo.getConfig();
        if (MapUtils.isNotEmpty(config)) {
            String user = config.getString("user");
            String password = config.getString("password");
            String url = config.getString("url");
            String driverClassName = config.getString("driverClassName");
            Properties props = new Properties();
            if (StringUtils.isNoneBlank(user)) {
                props.put("user", user);
            }
            if (StringUtils.isNotBlank(password)) {
                props.put("password", password);
            }
            JSONArray fileIdList = config.getJSONArray("fileIdList");
            if (CollectionUtils.isNotEmpty(fileIdList)) {
                URL[] urls = new URL[fileIdList.size()];
                for (int i = 0; i < fileIdList.size(); i++) {
                    Long fileId = fileIdList.getLong(i);
                    FileVo fileVo = fileMapper.getFileById(fileId);
                    if (fileVo == null) {
                        throw new FileNotFoundException(fileId);
                    }
                    try (InputStream is = FileUtil.getData(fileVo.getPath())) {
                        String fileName = fileVo.getName();
                        String suffix = ".jar";
                        String prefix = fileName.substring(0, fileName.length() - suffix.length());
                        File file = new File(prefix + "-" + fileVo.getId() + suffix);
                        Path path = Paths.get(file.toURI());
                        Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
                        urls[i] = file.toURI().toURL();
                    }catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        throw new RuntimeException(e);
                    }
                }
                URLClassLoader loader = new URLClassLoader(urls, null);
                Class<?> clazz = loader.loadClass(driverClassName);
                Driver driver = ((Driver) clazz.newInstance());
                return driver.connect(url, props);
            } else {
                throw new DatabaseConnectionFailedException(DatabaseConnectionFailedException.Type.FILE_ID_LIST_IS_EMPTY, dataBaseVo.getName());
            }
        } else {
            throw new DatabaseConnectionFailedException(DatabaseConnectionFailedException.Type.CONFIG_IS_EMPTY, dataBaseVo.getName());
        }
    }
}
