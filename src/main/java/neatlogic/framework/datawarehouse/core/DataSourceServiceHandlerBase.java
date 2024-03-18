/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.datawarehouse.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.datawarehouse.dao.mapper.DataWarehouseDataSourceDataMapper;
import neatlogic.framework.datawarehouse.dto.*;
import neatlogic.framework.datawarehouse.enums.AggregateType;
import neatlogic.framework.datawarehouse.enums.Mode;
import neatlogic.framework.util.javascript.JavascriptUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class DataSourceServiceHandlerBase implements IDataSourceServiceHandler {
    static Logger logger = LoggerFactory.getLogger(DataSourceServiceHandlerBase.class);

    protected static DataWarehouseDataSourceDataMapper dataSourceDataMapper;

    @Autowired
    public void setDataSourceDataMapper(DataWarehouseDataSourceDataMapper _dataSourceDataMapper) {
        dataSourceDataMapper = _dataSourceDataMapper;
    }


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

    protected List<SelectVo> getSqlFromDataSource(DataSourceVo reportDataSourceVo) throws DocumentException {
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

    @Override
    public void syncData(DataSourceVo dataSourceVo, DataSourceAuditVo reportDataSourceAuditVo) {
        //如果是替换模式，则需要先清理数据
        if (StringUtils.isNotBlank(dataSourceVo.getMode()) && dataSourceVo.getMode().equals(Mode.REPLACE.getValue())) {
            dataSourceDataMapper.truncateTable(dataSourceVo);
        }
        mySyncData(dataSourceVo, reportDataSourceAuditVo);
    }

    protected void mySyncData(DataSourceVo dataSourceVo, DataSourceAuditVo reportDataSourceAuditVo) {

    }

    /**
     * 同时拥有聚合计算字段和key字段的情况下才会进行聚合计算
     */
    protected void aggregateAndInsertData( List<DataSourceFieldVo> aggregateFieldList,List<DataSourceFieldVo> keyFieldList, DataSourceDataVo reportDataSourceDataVo, DataSourceAuditVo reportDataSourceAuditVo) {
        if (CollectionUtils.isNotEmpty(aggregateFieldList) && CollectionUtils.isNotEmpty(keyFieldList)) {
            Map<String, Object> aggregateMap = dataSourceDataMapper.getAggregateFieldValue(reportDataSourceDataVo);
            if (aggregateMap != null) {
                for (String key : aggregateMap.keySet()) {
                    Long aggregateFieldId = Long.parseLong(key.replace("field_", ""));
                    DataSourceFieldVo aggregateField = reportDataSourceDataVo.getFieldById(aggregateFieldId);
                    if (aggregateField != null) {
                        try {
                            BigDecimal v = new BigDecimal(0);
                            BigDecimal v2 = new BigDecimal(aggregateMap.get(key).toString()).setScale(4, RoundingMode.HALF_UP);
                            if (aggregateField.getAggregate().equals(AggregateType.COUNT.getValue())) {
                                v = new BigDecimal(1).setScale(4, RoundingMode.HALF_UP);
                            } else if (aggregateField.getAggregate().equals(AggregateType.SUM.getValue())) {
                                v = new BigDecimal(aggregateField.getValue().toString()).setScale(4, RoundingMode.HALF_UP);
                            }
                            aggregateField.setValue(v.floatValue() + v2.floatValue());
                        } catch (Exception ex) {
                            logger.error(ex.getMessage(), ex);
                        }
                    }
                }
            }
        }
        dataSourceDataMapper.insertDataSourceData(reportDataSourceDataVo);
        reportDataSourceAuditVo.addCount();
    }
}
