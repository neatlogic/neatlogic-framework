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

package neatlogic.framework.datawarehouse.utils;

import neatlogic.framework.datawarehouse.dto.DataSourceFieldVo;
import neatlogic.framework.datawarehouse.dto.DataSourceParamVo;
import neatlogic.framework.datawarehouse.dto.DataSourceVo;
import neatlogic.framework.datawarehouse.dto.ResultMapVo;
import neatlogic.framework.datawarehouse.exceptions.DataSourceXmlIrregularException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.*;

import java.util.*;

public class ReportXmlUtil {
    //static Logger logger = LoggerFactory.getLogger(ReportXmlUtil.class);

    private static void generateParamFromXml(List<Element> elementList, Set<String> checkSet, List<DataSourceParamVo> paramList) {
        for (Element sub : elementList) {
            if (sub.getName().equals("param")) {
                String name = sub.attributeValue("column");
                String label = sub.attributeValue("label");
                String defaultValue = sub.attributeValue("default");
                Long defaultValueLong = null;
                if (StringUtils.isNotBlank(defaultValue)) {
                    try {
                        defaultValueLong = Long.parseLong(defaultValue);
                    } catch (Exception ex) {
                        throw new DataSourceXmlIrregularException("“" + sub.getName() + "”节点的default属性需要是整形");
                    }
                }
                if (StringUtils.isBlank(name)) {
                    throw new DataSourceXmlIrregularException("“" + sub.getName() + "”节点必须定义唯一的“column”属性");
                } else {
                    if (checkSet.contains(name)) {
                        throw new DataSourceXmlIrregularException("column=" + name + "的" + sub.getName() + "节点已存在");
                    } else {
                        checkSet.add(name);
                    }
                }
                DataSourceParamVo param = new DataSourceParamVo();
                param.setName(name);
                param.setLabel(label);
                param.setDefaultValue(defaultValueLong);
                paramList.add(param);
            }
        }
    }

    private static void generateFieldFromXml(List<Element> elementList, Set<String> checkSet, List<DataSourceFieldVo> fieldList) {
        for (Element sub : elementList) {
            if (sub.getName().equals("id") || sub.getName().equals("field")) {
                String column = sub.attributeValue("column");
                String label = sub.attributeValue("label");
                String type = sub.attributeValue("type");
                String aggregate = sub.attributeValue("aggregate");
                if (StringUtils.isBlank(sub.attributeValue("column"))) {
                    throw new DataSourceXmlIrregularException("“" + sub.getName() + "”节点必须定义唯一的“column”属性");
                } else {
                    if (checkSet.contains(sub.attributeValue("column"))) {
                        throw new DataSourceXmlIrregularException("column=" + sub.attributeValue("column") + "的" + sub.getName() + "节点已存在");
                    } else {
                        checkSet.add(sub.attributeValue("column"));
                    }
                }
                fieldList.add(new DataSourceFieldVo(column, label, type, sub.getName().equals("id") ? 1 : 0, aggregate));
            }
        }
    }

    /**
     * @param xml 配置xml
     * @return 数据源对象
     */
    public static DataSourceVo generateDataSourceFromXml(String xml) throws DocumentException {
        Document document = DocumentHelper.parseText(xml);
        Element root = document.getRootElement();
        DataSourceVo reportDataSourceVo = new DataSourceVo();
        if (!root.getName().equalsIgnoreCase("datasource")) {
            throw new DataSourceXmlIrregularException("xml根节点名称必须是“datasource”");
        }


        Element fieldsElement = root.element("fields");
        Element paramsElement = root.element("params");
        //Element conditionsElement = root.element("conditions");
        Element selectElement = root.element("select");
        if (fieldsElement == null) {
            throw new DataSourceXmlIrregularException("请定义“fields”节点");
        } else {
            List<Element> subResultElList = fieldsElement.elements();
            Set<String> fieldCheckSet = new HashSet<>();
            if (CollectionUtils.isNotEmpty(subResultElList)) {
                List<DataSourceFieldVo> fieldList = new ArrayList<>();
                generateFieldFromXml(subResultElList, fieldCheckSet, fieldList);
                reportDataSourceVo.setFieldList(fieldList);
            } else {
                throw new DataSourceXmlIrregularException("fields节点必须包含id或field节点");
            }
        }

        if (paramsElement != null) {
            List<Element> paramElList = paramsElement.elements();
            Set<String> conditionCheckSet = new HashSet<>();
            if (CollectionUtils.isNotEmpty(paramElList)) {
                List<DataSourceParamVo> paramList = new ArrayList<>();
                generateParamFromXml(paramElList, conditionCheckSet, paramList);
                reportDataSourceVo.setParamList(paramList);
            } else {
                throw new DataSourceXmlIrregularException("params节点必须包含param节点");
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
