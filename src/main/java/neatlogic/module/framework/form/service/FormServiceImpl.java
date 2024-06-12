/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.module.framework.form.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.form.dao.mapper.FormMapper;
import neatlogic.framework.form.dto.AttributeDataVo;
import neatlogic.framework.form.dto.FormAttributeVo;
import neatlogic.framework.form.dto.FormVersionVo;
import neatlogic.framework.form.exception.FormActiveVersionNotFoundExcepiton;
import neatlogic.framework.form.service.IFormCrossoverService;
import neatlogic.framework.matrix.constvalue.SearchExpression;
import neatlogic.framework.matrix.core.IMatrixDataSourceHandler;
import neatlogic.framework.matrix.core.MatrixDataSourceHandlerFactory;
import neatlogic.framework.matrix.core.MatrixPrivateDataSourceHandlerFactory;
import neatlogic.framework.matrix.dao.mapper.MatrixMapper;
import neatlogic.framework.matrix.dto.*;
import neatlogic.framework.matrix.exception.MatrixDataSourceHandlerNotFoundException;
import neatlogic.framework.matrix.exception.MatrixNotFoundException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FormServiceImpl implements FormService, IFormCrossoverService {
    private static final Logger logger = LoggerFactory.getLogger(FormServiceImpl.class);

    @Resource
    private FormMapper formMapper;

    @Resource
    private MatrixMapper matrixMapper;

    @Override
    public JSONArray textConversionValueForSelectHandler(Object text, JSONObject config) {
        JSONArray valueList = new JSONArray();
        if (text == null) {
            return valueList;
        }
        String dataSource = config.getString("dataSource");
        if ("static".equals(dataSource)) {
            JSONArray dataArray = config.getJSONArray("dataList");
            if (CollectionUtils.isEmpty(dataArray)) {
                return null;
            }
            List<ValueTextVo> dataList = dataArray.toJavaList(ValueTextVo.class);
            Map<String, Object> valueTextMap = dataList.stream().collect(Collectors.toMap(ValueTextVo::getText, ValueTextVo::getValue));
            if (text instanceof String) {
                String textStr = (String) text;
                Object value = valueTextMap.get(textStr);
                if (value != null) {
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("value", value);
                    jsonObj.put("text", textStr);
                    valueList.add(jsonObj);
                }
                return valueList;
            } else if (text instanceof List) {
                List<String> textList = (List) text;
                if (CollectionUtils.isEmpty(textList)) {
                    return valueList;
                }
                for (String textStr : textList) {
                    Object value = valueTextMap.get(textStr);
                    if (value != null) {
                        JSONObject jsonObj = new JSONObject();
                        jsonObj.put("value", value);
                        jsonObj.put("text", textStr);
                        valueList.add(jsonObj);
                    }
                }
                return valueList;
            }
        } else if ("matrix".equals(dataSource)) {
            String matrixUuid = config.getString("matrixUuid");
            if (StringUtils.isBlank(matrixUuid)) {
                return valueList;
            }
            JSONObject mappingObj = config.getJSONObject("mapping");
            if (MapUtils.isEmpty(mappingObj)) {
                return valueList;
            }
            ValueTextVo mapping = mappingObj.toJavaObject(ValueTextVo.class);
            if (text instanceof String) {
                String textStr = (String) text;
                if (Objects.equals(mapping.getText(), mapping.getValue())) {
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("value", textStr);
                    jsonObj.put("text", textStr);
                    valueList.add(jsonObj);
                    return valueList;
                }
                String value = getValue(matrixUuid, mapping, textStr);
                if (value != null) {
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("value", value);
                    jsonObj.put("text", textStr);
                    valueList.add(jsonObj);
                }
                return valueList;
            } else if (text instanceof List) {
                List<String> textList = (List) text;
                if (CollectionUtils.isEmpty(textList)) {
                    return valueList;
                }
                for (String textStr : textList) {
                    if (Objects.equals(mapping.getText(), mapping.getValue())) {
                        JSONObject jsonObj = new JSONObject();
                        jsonObj.put("value", textStr);
                        jsonObj.put("text", textStr);
                        valueList.add(jsonObj);
                    } else {
                        String value = getValue(matrixUuid, mapping, textStr);
                        if (value != null) {
                            JSONObject jsonObj = new JSONObject();
                            jsonObj.put("value", value);
                            jsonObj.put("text", textStr);
                            valueList.add(jsonObj);
                        }
                    }
                }
                return valueList;
            }
        }
        return null;
    }

    @Override
    public JSONObject getMyDetailedDataForSelectHandler(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = new JSONObject();
        Object dataObj = attributeDataVo.getDataObj();
        if (dataObj == null) {
            return resultObj;
        }
        List<String> valueList = new ArrayList<>();
        List<String> textList = new ArrayList<>();
//        boolean isMultiple = configObj.getBooleanValue("isMultiple");
//        attributeDataVo.setIsMultiple(isMultiple ? 1 : 0);
        String dataSource = configObj.getString("dataSource");
        if ("static".equals(dataSource)) {
            JSONArray dataArray = configObj.getJSONArray("dataList");
            if (CollectionUtils.isEmpty(dataArray)) {
                return resultObj;
            }
            List<ValueTextVo> dataList = dataArray.toJavaList(ValueTextVo.class);
            Map<Object, String> valueTextMap = dataList.stream().collect(Collectors.toMap(ValueTextVo::getValue, ValueTextVo::getText));
            if (dataObj instanceof JSONArray) {
                JSONArray valueArray = (JSONArray) dataObj;
                if (CollectionUtils.isNotEmpty(valueArray)) {
//                    valueList = valueArray.toJavaList(String.class);
                    for (Object valueObj : valueArray) {
                        if (valueObj instanceof JSONObject) {
                            JSONObject jsonObj = (JSONObject) valueObj;
                            String value = jsonObj.getString("value");
                            if (value != null) {
                                valueList.add(value);
                            }
                            String text = jsonObj.getString("text");
                            if (text != null) {
                                textList.add(text);
                            } else {
                                if (value != null) {
                                    textList.add(value);
                                }
                            }
                        } else {
                            String text = valueTextMap.get(valueObj);
                            if (text != null) {
                                textList.add(text);
                            } else {
                                textList.add(valueObj.toString());
                            }
                        }
                    }
                }
            } else if (dataObj instanceof JSONObject) {
                JSONObject jsonObj = (JSONObject) dataObj;
                String value = jsonObj.getString("value");
                if (value != null) {
                    valueList.add(value);
                }
                String text = jsonObj.getString("text");
                if (text != null) {
                    textList.add(text);
                } else {
                    if (value != null) {
                        textList.add(value);
                    }
                }
            } else {
                String value = dataObj.toString();
                valueList.add(value);
                String text = valueTextMap.get(value);
                if (text != null) {
                    textList.add(text);
                } else {
                    textList.add(value);
                }
            }
        } else {// 其他，如动态数据源
            String matrixUuid = configObj.getString("matrixUuid");
            if (StringUtils.isBlank(matrixUuid)) {
                return resultObj;
            }
            JSONObject mappingObj = configObj.getJSONObject("mapping");
            if (MapUtils.isEmpty(mappingObj)) {
                return resultObj;
            }
            ValueTextVo mapping = mappingObj.toJavaObject(ValueTextVo.class);
            if (dataObj instanceof JSONArray) {
                JSONArray valueArray = (JSONArray) dataObj;
                if (CollectionUtils.isNotEmpty(valueArray)) {
                    for (int i = 0; i < valueArray.size(); i++) {
                        Object obj = valueArray.get(i);
                        if (obj instanceof JSONObject) {
                            JSONObject jsonObj = (JSONObject) obj;
                            String value = jsonObj.getString("value");
                            if (value != null) {
                                valueList.add(value);
                            }
                            String text = jsonObj.getString("text");
                            if (text != null) {
                                textList.add(text);
                            } else {
                                textList.add(value);
                            }
                        } else {
                            String value = obj.toString();
                            valueList.add(value);
                            String text = getText(matrixUuid, mapping, value);
                            if (text != null) {
                                textList.add(text);
                            } else {
                                textList.add(value);
                            }
                        }
                    }
                }
            } else if (dataObj instanceof JSONObject) {
                JSONObject jsonObj = (JSONObject) dataObj;
                String value = jsonObj.getString("value");
                if (value != null) {
                    valueList.add(value);
                }
                String text = jsonObj.getString("text");
                if (text != null) {
                    textList.add(text);
                } else {
                    textList.add(value);
                }
            } else {
                String value = dataObj.toString();
                valueList.add(value);
                String text = getText(matrixUuid, mapping, value);
                if (text != null) {
                    textList.add(text);
                } else {
                    textList.add(value);
                }
            }
        }
        resultObj.put("valueList", valueList);
        resultObj.put("textList", textList);
        return resultObj;
    }

    @Override
    public FormAttributeVo getFormAttributeByUuid(String uuid) {
        return formMapper.getFormAttributeByUuid(uuid);
    }

    private String getValue(String matrixUuid, ValueTextVo mapping, String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        try {
            MatrixVo matrixVo = MatrixPrivateDataSourceHandlerFactory.getMatrixVo(matrixUuid);
            if (matrixVo == null) {
                matrixVo = matrixMapper.getMatrixByUuid(matrixUuid);
                if (matrixVo == null) {
                    throw new MatrixNotFoundException(matrixUuid);
                }
            }
            IMatrixDataSourceHandler matrixDataSourceHandler = MatrixDataSourceHandlerFactory.getHandler(matrixVo.getType());
            if (matrixDataSourceHandler == null) {
                throw new MatrixDataSourceHandlerNotFoundException(matrixVo.getType());
            }
            String valueField = (String) mapping.getValue();
            String textField = mapping.getText();
            MatrixDataVo dataVo = new MatrixDataVo();
            dataVo.setMatrixUuid(matrixUuid);
            List<String> columnList = new ArrayList<>();
            columnList.add((String) mapping.getValue());
            columnList.add(mapping.getText());
            dataVo.setColumnList(columnList);
            dataVo.setKeyword(text);
            dataVo.setKeywordColumn(textField);
            for (int i = 0; i < 10; i++) {
                List<Map<String, JSONObject>> tbodyList = matrixDataSourceHandler.searchTableDataNew(dataVo);
                for (Map<String, JSONObject> tbody : tbodyList) {
                    JSONObject textObj = tbody.get(textField);
                    if (Objects.equals(text, textObj.getString("text"))) {
                        JSONObject valueObj = tbody.get(valueField);
                        return valueObj.getString("value");
                    }
                }
                if (dataVo.getCurrentPage() >= dataVo.getPageCount()) {
                    break;
                }
                dataVo.setCurrentPage(dataVo.getCurrentPage() + 1);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    private String getText(String matrixUuid, ValueTextVo mapping, String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }
        try {
            MatrixVo matrixVo = MatrixPrivateDataSourceHandlerFactory.getMatrixVo(matrixUuid);
            if (matrixVo == null) {
                matrixVo = matrixMapper.getMatrixByUuid(matrixUuid);
                if (matrixVo == null) {
                    throw new MatrixNotFoundException(matrixUuid);
                }
            }
            IMatrixDataSourceHandler matrixDataSourceHandler = MatrixDataSourceHandlerFactory.getHandler(matrixVo.getType());
            if (matrixDataSourceHandler == null) {
                throw new MatrixDataSourceHandlerNotFoundException(matrixVo.getType());
            }
            String valueField = (String) mapping.getValue();
            String textField = mapping.getText();
            MatrixDataVo dataVo = new MatrixDataVo();
            dataVo.setMatrixUuid(matrixUuid);
            List<String> columnList = new ArrayList<>();
            columnList.add((String) mapping.getValue());
            columnList.add(mapping.getText());
            dataVo.setColumnList(columnList);
            List<MatrixDefaultValueFilterVo> defaultValueFilterList = new ArrayList<>();
            MatrixDefaultValueFilterVo matrixDefaultValueFilterVo = new MatrixDefaultValueFilterVo(
                    new MatrixKeywordFilterVo(valueField, SearchExpression.EQ.getExpression(), value),
                    null
            );
            defaultValueFilterList.add(matrixDefaultValueFilterVo);
            dataVo.setDefaultValueFilterList(defaultValueFilterList);
            for (int i = 0; i < 10; i++) {
                List<Map<String, JSONObject>> tbodyList = matrixDataSourceHandler.searchTableDataNew(dataVo);
                for (Map<String, JSONObject> tbody : tbodyList) {
                    JSONObject valueObj = tbody.get(valueField);
                    if (Objects.equals(value, valueObj.getString("value"))) {
                        JSONObject textObj = tbody.get(textField);
                        return textObj.getString("text");
                    }
                }
                if (dataVo.getCurrentPage() >= dataVo.getPageCount()) {
                    break;
                }
                dataVo.setCurrentPage(dataVo.getCurrentPage() + 1);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    private JSONObject getSelectStandardValueBySimpleValue(String matrixUuid, ValueTextVo mapping, List<String> hiddenFieldList, String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            MatrixVo matrixVo = MatrixPrivateDataSourceHandlerFactory.getMatrixVo(matrixUuid);
            if (matrixVo == null) {
                matrixVo = matrixMapper.getMatrixByUuid(matrixUuid);
                if (matrixVo == null) {
                    throw new MatrixNotFoundException(matrixUuid);
                }
            }
            IMatrixDataSourceHandler matrixDataSourceHandler = MatrixDataSourceHandlerFactory.getHandler(matrixVo.getType());
            if (matrixDataSourceHandler == null) {
                throw new MatrixDataSourceHandlerNotFoundException(matrixVo.getType());
            }
            Set<String> notNullColumnSet = new HashSet<>();
            String valueField = (String) mapping.getValue();
            String textField = mapping.getText();
            MatrixDataVo dataVo = new MatrixDataVo();
            dataVo.setMatrixUuid(matrixUuid);
            notNullColumnSet.add(valueField);
            dataVo.setKeywordColumn(textField);
            notNullColumnSet.add(textField);
            List<String> columnList = new ArrayList<>();
            columnList.add(valueField);
            columnList.add(textField);
            if (CollectionUtils.isNotEmpty(hiddenFieldList)) {
                for (int i = 0; i < hiddenFieldList.size(); i++) {
                    String hiddenField = hiddenFieldList.get(i);
                    if (StringUtils.isNotBlank(hiddenField)) {
                        columnList.add(hiddenField);
                    }
                }
            }
            dataVo.setColumnList(columnList);
            dataVo.setNotNullColumnList(new ArrayList<>(notNullColumnSet));
            {
                List<MatrixDefaultValueFilterVo> defaultValueFilterList = new ArrayList<>();
                MatrixDefaultValueFilterVo matrixDefaultValueFilterVo = new MatrixDefaultValueFilterVo(
                        null,
                        new MatrixKeywordFilterVo(textField, SearchExpression.EQ.getExpression(), value)
                );
                defaultValueFilterList.add(matrixDefaultValueFilterVo);
                dataVo.setDefaultValueFilterList(defaultValueFilterList);
                for (int i = 0; i < 10; i++) {
                    List<Map<String, JSONObject>> tbodyList = matrixDataSourceHandler.searchTableDataNew(dataVo);
                    for (Map<String, JSONObject> tbody : tbodyList) {
                        JSONObject textObj = tbody.get(textField);
                        if (MapUtils.isEmpty(textObj)) {
                            continue;
                        }
                        if (Objects.equals(value, textObj.getString("text"))) {
                            JSONObject valueObj = tbody.get(valueField);
                            if (MapUtils.isEmpty(valueObj)) {
                                continue;
                            }
                            JSONObject resultObj = new JSONObject();
                            resultObj.put("value", valueObj.getString("value"));
                            resultObj.put("text", textObj.getString("text"));
                            for (String hiddenField : hiddenFieldList) {
                                JSONObject hiddenFieldObj = tbody.get(hiddenField);
                                if (MapUtils.isNotEmpty(hiddenFieldObj)) {
                                    String hiddenFieldValue = hiddenFieldObj.getString("value");
                                    resultObj.put(hiddenField, hiddenFieldValue);
                                }
                            }
                            return resultObj;
                        }
                    }
                    if (dataVo.getCurrentPage() >= dataVo.getPageCount()) {
                        break;
                    }
                    dataVo.setCurrentPage(dataVo.getCurrentPage() + 1);
                }
            }
            {
                List<MatrixDefaultValueFilterVo> defaultValueFilterList = new ArrayList<>();
                MatrixDefaultValueFilterVo matrixDefaultValueFilterVo = new MatrixDefaultValueFilterVo(
                        new MatrixKeywordFilterVo(valueField, SearchExpression.EQ.getExpression(), value),
                        null
                );
                defaultValueFilterList.add(matrixDefaultValueFilterVo);
                dataVo.setDefaultValueFilterList(defaultValueFilterList);
                for (int i = 0; i < 10; i++) {
                    List<Map<String, JSONObject>> tbodyList = matrixDataSourceHandler.searchTableDataNew(dataVo);
                    for (Map<String, JSONObject> tbody : tbodyList) {
                        JSONObject valueObj = tbody.get(valueField);
                        if (MapUtils.isEmpty(valueObj)) {
                            continue;
                        }
                        if (Objects.equals(value, valueObj.getString("value"))) {
                            JSONObject textObj = tbody.get(textField);
                            if (MapUtils.isEmpty(textObj)) {
                                continue;
                            }
                            JSONObject resultObj = new JSONObject();
                            resultObj.put("value", valueObj.getString("value"));
                            resultObj.put("text", textObj.getString("text"));
                            for (String hiddenField : hiddenFieldList) {
                                JSONObject hiddenFieldObj = tbody.get(hiddenField);
                                if (MapUtils.isNotEmpty(hiddenFieldObj)) {
                                    String hiddenFieldValue = hiddenFieldObj.getString("value");
                                    resultObj.put(hiddenField, hiddenFieldValue);
                                }
                            }
                            return resultObj;
                        }
                    }
                    if (dataVo.getCurrentPage() >= dataVo.getPageCount()) {
                        break;
                    }
                    dataVo.setCurrentPage(dataVo.getCurrentPage() + 1);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<FormAttributeVo> getFormAttributeList(String formUuid, String formName, String tag) {
        FormVersionVo formVersion = formMapper.getActionFormVersionByFormUuid(formUuid);
        if (formVersion == null) {
            throw new FormActiveVersionNotFoundExcepiton(formName);
        }
        FormAttributeVo searchVo = new FormAttributeVo();
        searchVo.setFormUuid(formUuid);
        searchVo.setFormVersionUuid(formVersion.getUuid());
        List<FormAttributeVo> formAttributeList = formMapper.getFormAttributeList(searchVo);
        if (StringUtils.isBlank(tag)) {
            return formAttributeList;
        }
        List<FormAttributeVo> resultList = new ArrayList<>();
        List<String> parentUuidList = new ArrayList<>();
        List<FormAttributeVo> formExtendAttributeList = new ArrayList<>();
        List<FormAttributeVo> list = formMapper.getFormExtendAttributeListByFormUuidAndFormVersionUuid(formUuid, formVersion.getUuid());
        for (FormAttributeVo formAttributeVo : list) {
            if (Objects.equals(formAttributeVo.getTag(), tag)) {
                formExtendAttributeList.add(formAttributeVo);
                parentUuidList.add(formAttributeVo.getParentUuid());
            }
        }
        for (FormAttributeVo formAttributeVo : formAttributeList) {
            if (parentUuidList.contains(formAttributeVo.getUuid())) {
                continue;
            }
            resultList.add(formAttributeVo);
        }
        resultList.addAll(formExtendAttributeList);
        return resultList;
    }

    @Override
    public Object getSelectStandardValueBySimpleValue(Object dataObj, JSONObject configObj) {
        JSONArray resultList = new JSONArray();
        if (Objects.equals(configObj.getString("dataSource"), "static")) {
            JSONArray dataArray = configObj.getJSONArray("dataList");
            if (CollectionUtils.isNotEmpty(dataArray)) {
                if (dataObj instanceof JSONArray) {
                    JSONArray valueArray = (JSONArray) dataObj;
                    if (CollectionUtils.isNotEmpty(valueArray)) {
                        for (Object valueObj : valueArray) {
                            if (valueObj instanceof JSONObject) {
                                JSONObject jsonObj = (JSONObject) valueObj;
                                if (dataArray.contains(jsonObj)) {
                                    resultList.add(jsonObj);
                                }
                            } else {
                                for (int i = 0; i < dataArray.size(); i++) {
                                    JSONObject jsonObj = dataArray.getJSONObject(i);
                                    if (MapUtils.isNotEmpty(jsonObj)) {
                                        if (jsonObj.containsValue(valueObj)) {
                                            resultList.add(jsonObj);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (dataObj instanceof JSONObject) {
                    JSONObject jsonObj = (JSONObject) dataObj;
                    if (dataArray.contains(jsonObj)) {
                        resultList.add(jsonObj);
                    }
                } else {
                    for (int i = 0; i < dataArray.size(); i++) {
                        JSONObject jsonObj = dataArray.getJSONObject(i);
                        if (MapUtils.isNotEmpty(jsonObj)) {
                            if (jsonObj.containsValue(dataObj)) {
                                resultList.add(jsonObj);
                            }
                        }
                    }
                }
            }
        } else {
            String matrixUuid = configObj.getString("matrixUuid");
            JSONObject mappingObj = configObj.getJSONObject("mapping");
            ValueTextVo mapping = mappingObj.toJavaObject(ValueTextVo.class);
            List<String> hiddenFieldUuidList = new ArrayList<>();
            JSONArray hiddenFieldArray = configObj.getJSONArray("hiddenFieldList");
            if (CollectionUtils.isNotEmpty(hiddenFieldArray)) {
                for (int i = 0; i < hiddenFieldArray.size(); i++) {
                    JSONObject hiddenFieldObj = hiddenFieldArray.getJSONObject(i);
                    if (MapUtils.isNotEmpty(hiddenFieldObj)) {
                        hiddenFieldUuidList.add(hiddenFieldObj.getString("uuid"));
                    }
                }
            }
            if (dataObj instanceof JSONArray) {
                JSONArray valueArray = (JSONArray) dataObj;
                if (CollectionUtils.isNotEmpty(valueArray)) {
                    for (int i = 0; i < valueArray.size(); i++) {
                        Object obj = valueArray.get(i);
                        if (obj instanceof JSONObject) {
                            JSONObject jsonObj = (JSONObject) obj;
                            resultList.add(jsonObj);
                        } else {
                            String value = obj.toString();
                            JSONObject jsonObj = getSelectStandardValueBySimpleValue(matrixUuid, mapping, hiddenFieldUuidList, value);
                            if (jsonObj != null) {
                                resultList.add(jsonObj);
                            }
                        }
                    }
                }
            } else if (dataObj instanceof JSONObject) {
                JSONObject jsonObj = (JSONObject) dataObj;
                resultList.add(jsonObj);
            } else {
                String value = dataObj.toString();
                JSONObject jsonObj = getSelectStandardValueBySimpleValue(matrixUuid, mapping, hiddenFieldUuidList, value);
                if (jsonObj != null) {
                    resultList.add(jsonObj);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(resultList)) {
            boolean isMultiple = configObj.getBooleanValue("isMultiple");
            if (isMultiple) {
                return resultList;
            } else {
                return resultList.get(0);
            }
        }
        return null;
    }
}
