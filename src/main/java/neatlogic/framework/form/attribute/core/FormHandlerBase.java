/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.form.attribute.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.util.RC4Util;
import neatlogic.framework.form.dto.AttributeDataVo;
import neatlogic.framework.form.exception.AttributeValidException;
import neatlogic.framework.form.exception.FormExtendAttributeConfigIllegalException;
import neatlogic.framework.matrix.dao.mapper.MatrixMapper;
import neatlogic.framework.util.$;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class FormHandlerBase implements IFormAttributeHandler, IFormAttributeDataConversionHandler {

    public enum ConversionType {

        TOVALUE("toValue", "text转换成value"),
        TOTEXT("toText", "value转换成text");

        private final String value;
        private final String text;

        public String getValue() {
            return value;
        }

        public String getText() {
            return $.t(text);
        }

        private ConversionType(String _value, String _text) {
            value = _value;
            text = _text;
        }
    }

    protected MatrixMapper matrixMapper;

    @Resource
    public void setMatrixMapper(MatrixMapper _matrixMapper) {
        this.matrixMapper = _matrixMapper;
    }

    @Override
    public final String getType() {
        return "form";
    }

    @Override
    public List<String> indexFieldContentList(String data) {
        List<String> contentList = myIndexFieldContentList(data);
        if (CollectionUtils.isEmpty(contentList)) {
            contentList = Collections.singletonList(data);
        }
        return contentList;
    }

    protected List<String> myIndexFieldContentList(String data) {
        return null;
    }

    @Override
    public Boolean isNeedSliceWord() {
        return true;
    }

    @Override
    public Boolean isUseFormConfig() {
        return true;
    }

    @Override
    public JSONObject getDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return getMyDetailedData(attributeDataVo, configObj);
    }

    protected abstract JSONObject getMyDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj);

    @Override
    public String getHandlerName() {
        return null;
    }

    @Override
    public String getIcon() {
        return null;
    }

    @Override
    public String getDataType() {
        return null;
    }

    @Override
    public boolean isShowable() {
        return false;
    }

    @Override
    public boolean isValueable() {
        return false;
    }

    @Override
    public boolean isFilterable() {
        return false;
    }

    @Override
    public boolean isExtendable() {
        return false;
    }

    @Override
    public boolean isForTemplate() {
        return false;
    }

    @Override
    public String getModule() {
        return null;
    }

    @Override
    public int getSort() {
        return 100;
    }

    protected String convertToString(Object source, String attributeLabel) {
        if (source == null) {
            return null;
        }
        if (source instanceof String) {
            return (String) source;
        } else if (source instanceof JSONArray && CollectionUtils.isNotEmpty((JSONArray) source)) {
            return ((JSONArray) source).getString(0);
        } else {
            return source.toString();
        }
    }

    protected JSONArray convertToJSONArray(Object source, String attributeLabel) {
        if (source == null) {
            return null;
        }
        if (source instanceof String) {
            try {
                return JSON.parseArray((String) source);
            } catch (Exception e) {
                throw new AttributeValidException(attributeLabel);
            }
        } else if (source instanceof JSONArray) {
            return (JSONArray) source;
        }
        throw new AttributeValidException(attributeLabel);
    }

    @Override
    public void validateExtendAttributeConfig(String key, JSONObject config) {
        if (config == null) {
            throw new FormExtendAttributeConfigIllegalException(this.getHandler(), key, "config");
        }
        myValidateExtendAttributeConfig(key, config);
    }

    protected void myValidateExtendAttributeConfig(String key, JSONObject config) {
    }

    /**
     * 组件内部密码组件密码解密
     * @param source
     * @param attributeUuid
     * @param otherParamConfig
     * @return
     */
    protected JSONObject withinPasswordDecryption(Object source, String attributeUuid, JSONObject otherParamConfig) {
        JSONObject resultObj = new JSONObject();
        JSONArray dataArray = null;
        if (source instanceof JSONArray) {
            dataArray = (JSONArray) source;
        }
        if (CollectionUtils.isEmpty(dataArray)) {
            return resultObj;
        }
        String rowUuid = otherParamConfig.getString("rowUuid");
        for (Object obj : dataArray) {
            if (obj instanceof JSONObject) {
                JSONObject dataObj = (JSONObject) obj;
                if (MapUtils.isNotEmpty(dataObj)) {
                    for (Map.Entry<String, Object> entry : dataObj.entrySet()) {
                        if (Objects.equals(entry.getKey(), "uuid")) {
                            continue;
                        }
                        if (Objects.equals(entry.getKey(), attributeUuid)) {
                            if (Objects.equals(dataObj.getString("uuid"), rowUuid)) {
                                String password = RC4Util.decrypt((String) entry.getValue());
                                resultObj.put("password", password);
                            }
                        } else {
                            if (entry.getValue() instanceof JSONArray) {
                                JSONObject passwordDecryptionObj = withinPasswordDecryption(entry.getValue(), attributeUuid, otherParamConfig);
                                if (MapUtils.isNotEmpty(passwordDecryptionObj)) {
                                    JSONArray parentUuidList = passwordDecryptionObj.getJSONArray("parentUuidList");
                                    if (parentUuidList == null) {
                                        parentUuidList = new JSONArray();
                                        passwordDecryptionObj.put("parentUuidList", parentUuidList);
                                    }
                                    parentUuidList.add(entry.getKey());
                                    return passwordDecryptionObj;
                                }
                            }
                        }
                    }
                }
            }
        }
        return resultObj;
    }
}
