/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.form.service;

import codedriver.framework.common.util.RC4Util;
import codedriver.framework.dependency.core.DependencyManager;
import codedriver.framework.form.attribute.core.FormAttributeHandlerFactory;
import codedriver.framework.form.attribute.core.IFormAttributeHandler;
import codedriver.framework.form.dao.mapper.FormMapper;
import codedriver.framework.form.dto.FormAttributeMatrixVo;
import codedriver.framework.form.dto.FormAttributeVo;
import codedriver.framework.form.exception.FormAttributeHandlerNotFoundException;
import codedriver.framework.form.service.IFormCrossoverService;
import codedriver.module.framework.dependency.handler.Integration2FormAttrDependencyHandler;
import codedriver.module.framework.dependency.handler.MatrixAttr2FormAttrDependencyHandler;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class FormServiceImpl implements FormService, IFormCrossoverService {

    @Resource
    private FormMapper formMapper;

    /**
     * 保存表单属性与其他功能的引用关系
     * @param formAttributeVo
     */
    @Override
    public void saveDependency(FormAttributeVo formAttributeVo) {
        String formUuid = formAttributeVo.getFormUuid();
        String formVersionUuid = formAttributeVo.getFormVersionUuid();
        IFormAttributeHandler formAttributeHandler = FormAttributeHandlerFactory.getHandler(formAttributeVo.getHandler());
        if (formAttributeHandler == null) {
            throw new FormAttributeHandlerNotFoundException(formAttributeVo.getHandler());
        }
        formAttributeHandler.makeupFormAttribute(formAttributeVo);
        Set<String> matrixUuidSet = formAttributeVo.getMatrixUuidSet();
        if (CollectionUtils.isNotEmpty(matrixUuidSet)) {
            for (String matrixUuid : matrixUuidSet) {
                FormAttributeMatrixVo formAttributeMatrixVo = new FormAttributeMatrixVo();
                formAttributeMatrixVo.setMatrixUuid(matrixUuid);
                formAttributeMatrixVo.setFormVersionUuid(formVersionUuid);
                formAttributeMatrixVo.setFormAttributeLabel(formAttributeVo.getLabel());
                formAttributeMatrixVo.setFormAttributeUuid(formAttributeVo.getUuid());
                formMapper.insertFormAttributeMatrix(formAttributeMatrixVo);
            }
        }

        Set<String> integrationUuidSet = formAttributeVo.getIntegrationUuidSet();
        if (CollectionUtils.isNotEmpty(integrationUuidSet)) {
            JSONObject config = new JSONObject();
            config.put("formUuid", formUuid);
            config.put("formVersionUuid", formVersionUuid);
            config.put("formAttributeUuid", formAttributeVo.getUuid());
            for (String integrationUuid : integrationUuidSet) {
                config.put("integrationUuid", integrationUuid);
                DependencyManager.insert(Integration2FormAttrDependencyHandler.class, integrationUuid, formAttributeVo.getUuid(), config);
            }
        }

        Map<String, Set<String>> matrixUuidAttributeUuidSetMap = formAttributeVo.getMatrixUuidAttributeUuidSetMap();
        if (MapUtils.isNotEmpty(matrixUuidAttributeUuidSetMap)) {
            JSONObject config = new JSONObject();
            config.put("formUuid", formUuid);
            config.put("formVersionUuid", formVersionUuid);
            for (Map.Entry<String, Set<String>> entry : matrixUuidAttributeUuidSetMap.entrySet()) {
                String matrixUuid = entry.getKey();
                config.put("matrixUuid", matrixUuid);
                Set<String> attributeUuidSet = entry.getValue();
                if (CollectionUtils.isNotEmpty(attributeUuidSet)) {
                    for (String attributeUuid : attributeUuidSet) {
                        DependencyManager.insert(MatrixAttr2FormAttrDependencyHandler.class, attributeUuid, formAttributeVo.getUuid(), config);
                    }
                }
            }
        }
    }

    @Override
    public JSONObject staticListPasswordEncrypt(JSONObject data, JSONObject config) {
        if (MapUtils.isEmpty(data)) {
            return data;
        }
        List<String> passwordTypeAttributeUuidList = new ArrayList<>();
        List<String> tableTypeAttributeUuidList = new ArrayList<>();
        JSONArray attributeList = config.getJSONArray("attributeList");
        if (CollectionUtils.isEmpty(attributeList)) {
            return data;
        }
        for (int i = 0; i < attributeList.size(); i++) {
            JSONObject attributeObj = attributeList.getJSONObject(i);
            if (MapUtils.isEmpty(attributeObj)) {
                continue;
            }
            String attributeUuid = attributeObj.getString("attributeUuid");
            String type = attributeObj.getString("type");
            if (Objects.equals(type, "password")) {
                passwordTypeAttributeUuidList.add(attributeUuid);
            } else if (Objects.equals(type, "table")) {
                tableTypeAttributeUuidList.add(attributeUuid);
                JSONObject attrConfig = attributeObj.getJSONObject("attrConfig");
                if (MapUtils.isEmpty(attrConfig)) {
                    continue;
                }
                JSONArray attributeArray = attrConfig.getJSONArray("attributeList");
                if (CollectionUtils.isEmpty(attributeArray)) {
                    continue;
                }
                for (int j = 0; j < attributeArray.size(); j++) {
                    JSONObject attributeObject = attributeArray.getJSONObject(j);
                    if (MapUtils.isEmpty(attributeObject)) {
                        continue;
                    }
                    String attrUuid = attributeObject.getString("attributeUuid");
                    if (Objects.equals(attributeObject.getString("type"), "password")) {
                        passwordTypeAttributeUuidList.add(attrUuid);
                    }
                }
            }
        }
        JSONObject extendedData = data.getJSONObject("extendedData");
        if (MapUtils.isNotEmpty(extendedData)) {
            for (Map.Entry<String, Object> entry : extendedData.entrySet()) {
               JSONObject rowDataObj = (JSONObject) entry.getValue();
               for (String key : rowDataObj.keySet()) {
                   if (passwordTypeAttributeUuidList.contains(key)) {
                       String value = rowDataObj.getString(key);
                       if (StringUtils.isNotBlank(value)) {
                           rowDataObj.put(key, RC4Util.encrypt(value));
                       }
                   } else if (tableTypeAttributeUuidList.contains(key)) {
                       JSONObject tableDataObj = rowDataObj.getJSONObject(key);
                       for (Map.Entry<String, Object> tableEntry : tableDataObj.entrySet()) {
                           JSONObject tableRowDataObj = (JSONObject) tableEntry.getValue();
                           List<String> keyList = new ArrayList<>(tableRowDataObj.keySet());
                           for (String tableRowKey : keyList) {
                               if (passwordTypeAttributeUuidList.contains(tableRowKey)) {
                                   String value = tableRowDataObj.getString(tableRowKey);
                                   if (StringUtils.isNotBlank(value)) {
                                       tableRowDataObj.put(tableRowKey, RC4Util.encrypt(value));
                                   }
                               }
                           }
                       }
                   }
               }
            }
        }
        JSONObject detailData = data.getJSONObject("detailData");
        if (MapUtils.isNotEmpty(detailData)) {
            for (Map.Entry<String, Object> entry : detailData.entrySet()) {
                JSONObject rowDataObj = (JSONObject) entry.getValue();
                for (String key : rowDataObj.keySet()) {
                    if (passwordTypeAttributeUuidList.contains(key)) {
                        JSONObject valueObj = rowDataObj.getJSONObject(key);
                        if (MapUtils.isNotEmpty(valueObj)) {
                            String value = valueObj.getString("value");
                            if (StringUtils.isNotBlank(value)) {
                                valueObj.put("value", RC4Util.encrypt(value));
                            }
                            String text = valueObj.getString("text");
                            if (StringUtils.isNotBlank(text)) {
                                valueObj.put("text", RC4Util.encrypt(text));
                            }
                        }
                    } else if (tableTypeAttributeUuidList.contains(key)) {
                        JSONObject tableDataObj = rowDataObj.getJSONObject(key);
                        tableDataObj = tableDataObj.getJSONObject("value");
                        for (Map.Entry<String, Object> tableEntry : tableDataObj.entrySet()) {
                            JSONObject tableRowDataObj = (JSONObject) tableEntry.getValue();
                            for (String tableRowKey : tableRowDataObj.keySet()) {
                                if (passwordTypeAttributeUuidList.contains(tableRowKey)) {
                                    JSONObject valueObj = tableRowDataObj.getJSONObject(tableRowKey);
                                    if (MapUtils.isNotEmpty(valueObj)) {
                                        String value = valueObj.getString("value");
                                        if (StringUtils.isNotBlank(value)) {
                                            valueObj.put("value", RC4Util.encrypt(value));
                                        }
                                        String text = valueObj.getString("text");
                                        if (StringUtils.isNotBlank(text)) {
                                            valueObj.put("text", RC4Util.encrypt(text));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return data;
    }
}
