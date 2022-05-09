/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.datawarehouse.utils;

import codedriver.framework.datawarehouse.dto.ReportDataSourceConditionVo;
import codedriver.framework.datawarehouse.dto.ReportDataSourceFieldVo;
import codedriver.framework.datawarehouse.dto.ReportDataSourceVo;
import codedriver.framework.datawarehouse.dto.ResultMapVo;
import codedriver.framework.datawarehouse.exceptions.DataSourceXmlIrregularException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.*;

import java.util.*;

public class ReportXmlUtil {
    //static Logger logger = LoggerFactory.getLogger(ReportXmlUtil.class);

    private static void generateConditionFromXml(List<Element> elementList, Set<String> checkSet, List<ReportDataSourceConditionVo> conditionList) {
        for (Element sub : elementList) {
            if (sub.getName().equals("condition")) {
                String name = sub.attributeValue("name");
                String label = sub.attributeValue("label");
                String type = sub.attributeValue("type");
                String isRequired = sub.attributeValue("isRequired");
                if (StringUtils.isBlank(sub.attributeValue("name"))) {
                    throw new DataSourceXmlIrregularException("“" + sub.getName() + "”节点必须定义唯一的“name”属性");
                } else {
                    if (checkSet.contains(sub.attributeValue("name"))) {
                        throw new DataSourceXmlIrregularException("name=" + sub.attributeValue("name") + "的" + sub.getName() + "节点已存在");
                    } else {
                        checkSet.add(sub.attributeValue("name"));
                    }
                }
                ReportDataSourceConditionVo condition = new ReportDataSourceConditionVo(name, label, type);
                if (StringUtils.isNotBlank(isRequired)) {
                    condition.setIsRequired(isRequired.equals("1") || isRequired.equalsIgnoreCase("true") ? 1 : 0);
                }
                conditionList.add(condition);
            }
        }
    }

    private static void generateFieldFromXml(List<Element> elementList, Set<String> checkSet, List<ReportDataSourceFieldVo> fieldList) {
        for (Element sub : elementList) {
            if (sub.getName().equals("id") || sub.getName().equals("field")) {
                String column = sub.attributeValue("column");
                String label = sub.attributeValue("label");
                String type = sub.attributeValue("type");
                if (StringUtils.isBlank(sub.attributeValue("column"))) {
                    throw new DataSourceXmlIrregularException("“" + sub.getName() + "”节点必须定义唯一的“column”属性");
                } else {
                    if (checkSet.contains(sub.attributeValue("column"))) {
                        throw new DataSourceXmlIrregularException("column=" + sub.attributeValue("column") + "的" + sub.getName() + "节点已存在");
                    } else {
                        checkSet.add(sub.attributeValue("column"));
                    }
                }
                fieldList.add(new ReportDataSourceFieldVo(column, label, type, sub.getName().equals("id") ? 1 : 0));
            }
        }
    }

    /**
     * @param xml 配置xml
     * @return 数据源对象
     */
    public static ReportDataSourceVo generateDataSourceFromXml(String xml) throws DocumentException {
        Document document = DocumentHelper.parseText(xml);
        Element root = document.getRootElement();
        ReportDataSourceVo reportDataSourceVo = new ReportDataSourceVo();
        if (!root.getName().equals("report")) {
            throw new DataSourceXmlIrregularException("xml根节点名称必须是“report”");
        }


        Element fieldsElement = root.element("fields");
        Element conditionsElement = root.element("conditions");
        Element selectElement = root.element("select");
        if (fieldsElement == null) {
            throw new DataSourceXmlIrregularException("请定义“fields”节点");
        } else {
            List<Element> subResultElList = fieldsElement.elements();
            Set<String> fieldCheckSet = new HashSet<>();
            if (CollectionUtils.isNotEmpty(subResultElList)) {
                List<ReportDataSourceFieldVo> fieldList = new ArrayList<>();
                generateFieldFromXml(subResultElList, fieldCheckSet, fieldList);
                reportDataSourceVo.setFieldList(fieldList);
            } else {
                throw new DataSourceXmlIrregularException("fields节点必须包含id或field节点");
            }
        }

        if (conditionsElement != null) {
            List<Element> conditionElList = conditionsElement.elements();
            Set<String> conditionCheckSet = new HashSet<>();
            if (CollectionUtils.isNotEmpty(conditionElList)) {
                List<ReportDataSourceConditionVo> conditionList = new ArrayList<>();
                generateConditionFromXml(conditionElList, conditionCheckSet, conditionList);
                reportDataSourceVo.setConditionList(conditionList);
            } else {
                throw new DataSourceXmlIrregularException("conditions节点必须包含condition节点");
            }
        }

        if (selectElement == null) {
            throw new DataSourceXmlIrregularException("请定义一个“select”节点");
        }


        List<Node> ifNotNullElementList = document.selectNodes("//ifNotNull");
        List<Node> ifNullElementList = document.selectNodes("//ifNull");
        List<Node> forEachElementList = document.selectNodes("//forEach");
        List<Node> ifElementList = document.selectNodes("//if");

        if (ifNotNullElementList != null && ifNotNullElementList.size() > 0) {
            for (Node node : ifNotNullElementList) {
                Element e = (Element) node;
                if (StringUtils.isBlank(e.attributeValue("parameter"))) {
                    throw new DataSourceXmlIrregularException("“ifNotNull”节点的“parameter”属性不能为空。");
                }
            }
        }

        if (ifNullElementList != null && ifNullElementList.size() > 0) {
            for (Node node : ifNullElementList) {
                Element e = (Element) node;
                if (StringUtils.isBlank(e.attributeValue("parameter"))) {
                    throw new DataSourceXmlIrregularException("“ifNull”节点的“parameter”属性不能为空。");
                }
            }
        }

        if (forEachElementList != null && forEachElementList.size() > 0) {
            for (Node node : forEachElementList) {
                Element e = (Element) node;
                if (StringUtils.isBlank(e.attributeValue("parameter")) || StringUtils.isBlank(e.attributeValue("separator"))) {
                    throw new DataSourceXmlIrregularException("“forEach”节点的“parameter”和“separator”属性不能为空。");
                }
            }
        }

        if (ifElementList != null && ifElementList.size() > 0) {
            for (Node node : ifElementList) {
                Element e = (Element) node;
                if (StringUtils.isBlank(e.attributeValue("test"))) {
                    throw new DataSourceXmlIrregularException("“if”节点的“test”属性不能为空。");
                }
            }
        }

        return reportDataSourceVo;
    }

    private static ResultMapVo analyseResultMap(Element element) {
        ResultMapVo resultMapVo = new ResultMapVo();
        String resultMapId = element.attributeValue("id");
        List<Element> resultNodeList = element.elements();
        resultMapVo.setId(resultMapId);
        for (Element result : resultNodeList) {
            if (result.getName().equals("id")) {
                resultMapVo.addGroupBy(result.attributeValue("property"));
                resultMapVo.addProperty(result.attributeValue("property"));
            } else if (result.getName().equals("result")) {
                resultMapVo.addProperty(result.attributeValue("property"));
            } else if (result.getName().equals("collection")) {
                Map<String, ResultMapVo> map = resultMapVo.getResultMap();
                if (map == null) {
                    map = new HashMap<>();
                }
                map.put(result.attributeValue("property"), analyseResultMap(result));
                resultMapVo.setResultMap(map);
            }
        }
        return resultMapVo;
    }


}
