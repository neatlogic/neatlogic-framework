/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.datawarehouse.service;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.datawarehouse.dao.mapper.*;
import codedriver.framework.datawarehouse.dto.*;
import codedriver.framework.datawarehouse.enums.AggregateType;
import codedriver.framework.datawarehouse.enums.DatabaseVersion;
import codedriver.framework.datawarehouse.enums.Mode;
import codedriver.framework.datawarehouse.enums.Status;
import codedriver.framework.datawarehouse.exceptions.*;
import codedriver.framework.scheduler.core.IJob;
import codedriver.framework.scheduler.core.SchedulerManager;
import codedriver.framework.scheduler.dto.JobObject;
import codedriver.framework.transaction.core.AfterTransactionJob;
import codedriver.framework.transaction.core.EscapeTransactionJob;
import codedriver.framework.util.javascript.JavascriptUtil;
import codedriver.module.framework.scheduler.datawarehouse.ReportDataSourceJob;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DataSourceServiceImpl implements DataSourceService {
    int FETCH_SIZE = 1000;
    static Logger logger = LoggerFactory.getLogger(DataSourceServiceImpl.class);
    @Resource
    private DataSource dataSource;

    @Resource
    private DataWarehouseConnectionMapper reportConnectionMapper;

    @Resource
    private DataWarehouseDataSourceMapper dataSourceMapper;

    @Resource
    private DataWarehouseDataSourceDataMapper dataSourceDataMapper;

    @Resource
    private DataWarehouseDataSourceSchemaMapper dataSourceSchemaMapper;

    @Resource
    private DataWarehouseDataSourceAuditMapper dataSourceAuditMapper;

    @Resource
    private SchedulerManager schedulerManager;

    private static boolean evaluateExpression(String expression, Map<String, Object> paramMap) {
        ScriptEngine engine = JavascriptUtil.getEngine();
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            engine.put(entry.getKey(), entry.getValue());
        }
        try {
            return Boolean.parseBoolean(engine.eval(expression).toString());
        } catch (ScriptException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }


    private List<SelectVo> getSqlFromDataSource(DataSourceVo reportDataSourceVo) throws DocumentException {
        Document document = DocumentHelper.parseText(reportDataSourceVo.getXml());
        Element root = document.getRootElement();
        List<Element> selectElementList = root.elements("select");
        String regex_param = "#\\{([^}]+?)}";
        String[] replace_regex = new String[]{"<[/]?if[^>]*?>", "<[/]?select[^>]*?>", "<[/]?forEach[^>]*?>", "<[/]?ifNotNull[^>]*?>", "<[/]?ifNull[^>]*?>", "<[/]?forEach[^>]*?>", "\\<\\!\\[CDATA\\[", "\\]\\]\\>"};
        JSONObject paramMap = new JSONObject();
        if (CollectionUtils.isNotEmpty(reportDataSourceVo.getParamList())) {
            for (DataSourceParamVo paramVo : reportDataSourceVo.getParamList()) {
                paramMap.put(paramVo.getName(), paramVo.getCurrentValue() == null ? paramVo.getDefaultValue() : paramVo.getCurrentValue());
            }
        }
        List<SelectVo> selectList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(selectElementList)) {
            for (Element selectElement : selectElementList) {
                SelectVo selectVo = new SelectVo();
                List<Element> ifElList = selectElement.elements("if");
                if (CollectionUtils.isNotEmpty(ifElList)) {
                    List<Element> removeObj = new ArrayList<>();
                    for (Element ifEl : ifElList) {
                        String testExp = ifEl.attributeValue("test");
                        if (!evaluateExpression(testExp, paramMap)) {
                            removeObj.add(ifEl);
                        }
                    }
                    for (Element i : removeObj) {
                        ifElList.remove(i);
                    }
                }

                List<Element> forEachList = selectElement.elements("forEach");
                if (CollectionUtils.isNotEmpty(forEachList)) {
                    List<Element> removeObj = new ArrayList<>();
                    for (Element foreachEl : forEachList) {
                        boolean hasParam = false;
                        Object p = paramMap.get(foreachEl.attributeValue("parameter"));
                        if (p != null) {
                            hasParam = true;
                            String separator = foreachEl.attributeValue("separator");
                            String orgText = foreachEl.getText();
                            StringBuilder newText = new StringBuilder();
                            if (p instanceof String || p instanceof Number) {
                                newText = new StringBuilder(orgText);
                            } else if (p instanceof String[]) {
                                for (int pi = 0; pi < ((String[]) p).length; pi++) {
                                    String replaceValue = orgText.replace("#{" + foreachEl.attributeValue("parameter") + "}", "#{" + foreachEl.attributeValue("parameter") + "#" + pi + "}");
                                    if (!newText.toString().contains(replaceValue)) {//防止参数重复拼接
                                        newText.append(replaceValue).append(separator);
                                    }
                                }
                            } else if (p instanceof List<?>) {
                                int i = 0;
                                for (String pi : (List<String>) p) {
                                    String replaceValue = orgText.replace("#{" + foreachEl.attributeValue("parameter") + "}", "#{" + foreachEl.attributeValue("parameter") + "#" + pi + "}");
                                    if (newText.toString().contains(replaceValue)) {//防止参数重复拼接
                                        newText.append(replaceValue).append(separator);
                                    }
                                    i++;
                                }
                            }
                            //统一处理
                            if (newText.toString().endsWith(separator)) {
                                newText = new StringBuilder(newText.substring(0, newText.length() - separator.length()));
                            }
                            foreachEl.setText(newText.toString());
                        }
                        if (!hasParam) {
                            removeObj.add(foreachEl);
                        }
                    }
                    for (Element i : removeObj) {
                        forEachList.remove(i);
                    }
                }
                String result = selectElement.asXML();
                StringBuffer temp;
                Pattern pattern;
                Matcher matcher;
                for (String regex : replace_regex) {
                    temp = new StringBuffer();
                    pattern = Pattern.compile(regex, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
                    matcher = pattern.matcher(result);
                    while (matcher.find()) {
                        matcher.appendReplacement(temp, "");
                    }
                    matcher.appendTail(temp);
                    result = temp.toString();
                }

                temp = new StringBuffer();
                pattern = Pattern.compile(regex_param, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
                matcher = pattern.matcher(result);
                List<Object> paramList = new ArrayList<>();
                while (matcher.find()) {
                    String key = matcher.group(1);
                    if (key.contains("#")) {
                        key = key.substring(0, key.lastIndexOf("#"));
                    }
                    Object pp = paramMap.get(key);
                    if (pp instanceof String || pp instanceof Number) {
                        matcher.appendReplacement(temp, "?");
                        paramList.add(pp);
                    } else if (pp instanceof String[]) {
                        if (matcher.group(1).contains("#")) {
                            int s = Integer.parseInt(matcher.group(1).substring(matcher.group(1).lastIndexOf("#") + 1));
                            String[] ps = (String[]) pp;
                            if (ps[s] != null) {
                                paramList.add(ps[s]);
                                matcher.appendReplacement(temp, "?");
                            }
                        } else {
                            matcher.appendReplacement(temp, "?");
                            paramList.add(((String[]) pp)[0]);
                        }
                    } else if (pp instanceof Number[]) {
                        if (matcher.group(1).contains("#")) {
                            int s = Integer.parseInt(matcher.group(1).substring(matcher.group(1).lastIndexOf("#") + 1));
                            Number[] ps = (Number[]) pp;
                            if (ps[s] != null) {
                                paramList.add(ps[s]);
                                matcher.appendReplacement(temp, "?");
                            }
                        } else {
                            matcher.appendReplacement(temp, "?");
                            paramList.add(((Number[]) pp)[0]);
                        }
                    } else if (pp instanceof List<?>) {
                        if (matcher.group(1).contains("#")) {
                            int s = Integer.parseInt(matcher.group(1).substring(matcher.group(1).lastIndexOf("#") + 1));
                            List<String> ps = (List<String>) pp;
                            if (ps.get(s) != null) {
                                paramList.add(ps.get(s));
                                matcher.appendReplacement(temp, "?");
                            }
                        } else {
                            matcher.appendReplacement(temp, "?");
                            paramList.add(((List<String>) pp).get(0));
                        }
                    }
                }
                matcher.appendTail(temp);

                selectVo.setParamList(paramList);
                selectVo.setSql(temp.toString());
                selectVo.setSql(selectVo.getSql().replace("&gt;", ">").replace("&lt;", "<"));
                selectVo.setParamMap(paramMap);
                selectList.add(selectVo);
            }
        }
        return selectList;
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

    @Override
    public void deleteReportDataSource(DataSourceVo reportDataSourceVo) {
        if (reportDataSourceVo != null) {
            dataSourceMapper.deleteDataSourceFieldByDataSourceId(reportDataSourceVo.getId());
            dataSourceMapper.deleteReportDataSourceById(reportDataSourceVo.getId());
            dataSourceAuditMapper.deleteReportDataSourceAuditByDatasourceId(reportDataSourceVo.getId());
            //由于以下操作是DDL操作，所以需要使用EscapeTransactionJob避开当前事务，否则在进行DDL操作之前事务就会提交，如果DDL出错，则上面的事务就无法回滚了
            EscapeTransactionJob.State s = new EscapeTransactionJob(() -> dataSourceSchemaMapper.deleteDataSourceTable(reportDataSourceVo)).execute();
            if (!s.isSucceed()) {
                throw new DeleteDataSourceSchemaException(reportDataSourceVo);
            }
        }
    }

    @Override
    public void executeReportDataSource(DataSourceVo pDataSourceVo) {
        if (pDataSourceVo != null && CollectionUtils.isNotEmpty(pDataSourceVo.getFieldList())) {
            if (Objects.equals(pDataSourceVo.getStatus(), Status.DOING.getValue())) {
                throw new ReportDataSourceIsSyncingException(pDataSourceVo);
            }
            //更新数据源状态，写入审计信息
            pDataSourceVo.setStatus(Status.DOING.getValue());
            dataSourceMapper.updateReportDataSourceStatus(pDataSourceVo);
            DataSourceAuditVo reportDataSourceAuditVo = new DataSourceAuditVo();
            reportDataSourceAuditVo.setDataSourceId(pDataSourceVo.getId());
            dataSourceAuditMapper.insertReportDataSourceAudit(reportDataSourceAuditVo);
            AfterTransactionJob<DataSourceVo> afterTransactionJob = new AfterTransactionJob<>("REPORT-DATASOURCE-SYNC");
            afterTransactionJob.execute(pDataSourceVo, dataSourceVo -> {
                //如果是替换模式，则需要先清理数据
                if (StringUtils.isNotBlank(dataSourceVo.getMode()) && dataSourceVo.getMode().equals(Mode.REPLACE.getValue())) {
                    dataSourceDataMapper.truncateTable(dataSourceVo);
                }

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
                        /*
                        同时拥有聚合计算字段和key字段的情况下才会进行聚合计算
                         */
                            if (CollectionUtils.isNotEmpty(aggregateFieldList) && CollectionUtils.isNotEmpty(keyFieldList)) {
                                Map<String, Object> aggregateMap = dataSourceDataMapper.getAggregateFieldValue(reportDataSourceDataVo);
                                if (aggregateMap != null) {
                                    for (String key : aggregateMap.keySet()) {
                                        Long aggregateFieldId = Long.parseLong(key.replace("field_", ""));
                                        DataSourceFieldVo aggregateField = reportDataSourceDataVo.getFieldById(aggregateFieldId);
                                        if (aggregateField != null) {
                                            if (aggregateField.getAggregate().equals(AggregateType.COUNT.getValue())) {
                                                try {
                                                    BigDecimal v = new BigDecimal(aggregateField.getValue().toString()).setScale(4, RoundingMode.HALF_UP);
                                                    aggregateField.setValue(v.floatValue() + 1);
                                                } catch (Exception ex) {
                                                    logger.error(ex.getMessage(), ex);
                                                }
                                            } else if (aggregateField.getAggregate().equals(AggregateType.SUM.getValue())) {
                                                try {
                                                    BigDecimal v1 = new BigDecimal(aggregateField.getValue().toString()).setScale(4, RoundingMode.HALF_UP);
                                                    BigDecimal v2 = new BigDecimal(aggregateMap.get(key).toString()).setScale(4, RoundingMode.HALF_UP);
                                                    aggregateField.setValue(v1.floatValue() + v2.floatValue());
                                                } catch (Exception ex) {
                                                    logger.error(ex.getMessage(), ex);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            dataSourceDataMapper.insertDataSourceData(reportDataSourceDataVo);
                            reportDataSourceAuditVo.addCount();
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
                    dataSourceVo.setStatus(Status.DONE.getValue());
                    int dataCount = dataSourceDataMapper.getDataSourceDataCount(new DataSourceDataVo(dataSourceVo.getId()));
                    dataSourceVo.setDataCount(dataCount);
                    dataSourceMapper.updateReportDataSourceStatus(dataSourceVo);
                    dataSourceAuditMapper.updateReportDataSourceAudit(reportDataSourceAuditVo);
                }
            });
        }
    }

    @Override
    public void insertDataSource(DataSourceVo vo) {
        dataSourceMapper.insertDataSource(vo);
        if (CollectionUtils.isNotEmpty(vo.getFieldList())) {
            for (DataSourceFieldVo field : vo.getFieldList()) {
                field.setDataSourceId(vo.getId());
                dataSourceMapper.insertDataSourceField(field);
            }
        }
        if (CollectionUtils.isNotEmpty(vo.getParamList())) {
            for (DataSourceParamVo param : vo.getParamList()) {
                param.setDataSourceId(vo.getId());
                dataSourceMapper.insertDataSourceParam(param);
            }
        }
    }

    @Override
    public void updateDataSource(DataSourceVo newVo, DataSourceVo newXmlConfig, DataSourceVo oldVo) {
        List<DataSourceFieldVo> deleteFieldList = oldVo.getFieldList().stream().filter(d -> !newXmlConfig.getFieldList().contains(d)).collect(Collectors.toList());
        List<DataSourceFieldVo> updateFieldList = newXmlConfig.getFieldList().stream().filter(d -> oldVo.getFieldList().contains(d)).collect(Collectors.toList());
        List<DataSourceFieldVo> insertFieldList = newXmlConfig.getFieldList().stream().filter(d -> !oldVo.getFieldList().contains(d)).collect(Collectors.toList());
        //用回旧的fieldId
        if (CollectionUtils.isNotEmpty(updateFieldList)) {
            for (DataSourceFieldVo field : updateFieldList) {
                Optional<DataSourceFieldVo> op = oldVo.getFieldList().stream().filter(d -> d.equals(field)).findFirst();
                op.ifPresent(dataSourceFieldVo -> field.setId(dataSourceFieldVo.getId()));
            }
        }
        newVo.setFieldList(null);//清空旧数据
        newVo.addField(insertFieldList);
        newVo.addField(updateFieldList);

        List<DataSourceParamVo> deleteParamList = oldVo.getParamList().stream().filter(d -> !newXmlConfig.getParamList().contains(d)).collect(Collectors.toList());
        List<DataSourceParamVo> updateParamList = newXmlConfig.getParamList().stream().filter(d -> oldVo.getParamList().contains(d)).collect(Collectors.toList());
        List<DataSourceParamVo> insertParamList = newXmlConfig.getParamList().stream().filter(d -> !oldVo.getParamList().contains(d)).collect(Collectors.toList());
        //用回旧的paramId
        if (CollectionUtils.isNotEmpty(updateParamList)) {
            for (DataSourceParamVo param : updateParamList) {
                Optional<DataSourceParamVo> op = oldVo.getParamList().stream().filter(d -> d.equals(param)).findFirst();
                op.ifPresent(dataSourceParamVo -> param.setId(dataSourceParamVo.getId()));
            }
        }
        newVo.setParamList(null);//清空旧数据
        newVo.addParam(insertParamList);
        newVo.addParam(updateParamList);

        //FIXME 检查数据源是否被使用
        dataSourceMapper.updateDataSource(newVo);

        if (CollectionUtils.isNotEmpty(deleteFieldList)) {
            for (DataSourceFieldVo field : deleteFieldList) {
                dataSourceMapper.deleteDataSourceFieldById(field.getId());
            }
        }

        if (CollectionUtils.isNotEmpty(updateFieldList)) {
            for (DataSourceFieldVo field : updateFieldList) {
                dataSourceMapper.updateDataSourceField(field);
            }
        }

        if (CollectionUtils.isNotEmpty(insertFieldList)) {
            for (DataSourceFieldVo field : insertFieldList) {
                field.setDataSourceId(newVo.getId());
                dataSourceMapper.insertDataSourceField(field);
            }
        }

        if (CollectionUtils.isNotEmpty(deleteParamList)) {
            for (DataSourceParamVo param : deleteParamList) {
                dataSourceMapper.deleteDataSourceParamById(param.getId());
            }
        }

        if (CollectionUtils.isNotEmpty(updateParamList)) {
            for (DataSourceParamVo param : updateParamList) {
                dataSourceMapper.updateDataSourceParam(param);
            }
        }

        if (CollectionUtils.isNotEmpty(insertParamList)) {
            for (DataSourceParamVo param : insertParamList) {
                param.setDataSourceId(newVo.getId());
                dataSourceMapper.insertDataSourceParam(param);
            }
        }
    }

    @Override
    public void createDataSourceSchema(DataSourceVo dataSourceVo) {
        //由于以下操作是DDL操作，所以需要使用EscapeTransactionJob避开当前事务，否则在进行DDL操作之前事务就会提交，如果DDL出错，则上面的事务就无法回滚了
        EscapeTransactionJob.State s = new EscapeTransactionJob(() -> {
            dataSourceSchemaMapper.deleteDataSourceTable(dataSourceVo);
            dataSourceSchemaMapper.createDataSourceTable(dataSourceVo);
        }).execute();
        if (!s.isSucceed()) {
            throw new CreateDataSourceSchemaException(dataSourceVo);
        }
    }

    @Override
    public void loadOrUnloadReportDataSourceJob(DataSourceVo dataSourceVo) {
        String tenantUuid = TenantContext.get().getTenantUuid();
        IJob jobHandler = SchedulerManager.getHandler(ReportDataSourceJob.class.getName());
        JobObject jobObject = new JobObject.Builder(dataSourceVo.getId().toString(), jobHandler.getGroupName(), jobHandler.getClassName(), tenantUuid)
                .withCron(dataSourceVo.getCronExpression())
                .addData("datasourceId", dataSourceVo.getId())
                .build();
        if (StringUtils.isNotBlank(dataSourceVo.getCronExpression())) {
            schedulerManager.loadJob(jobObject);
        } else {
            schedulerManager.unloadJob(jobObject);
        }
    }
}
