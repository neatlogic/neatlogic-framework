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

package neatlogic.framework.form.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.form.attribute.core.FormAttributeHandlerFactory;
import neatlogic.framework.form.attribute.core.IFormAttributeHandler;
import neatlogic.framework.util.SnowflakeUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class AttributeDataVo implements Comparable<AttributeDataVo>, Serializable {
    private Long id;
    private String formUuid;
    private String tag;
    private String attributeUuid;
    private String attributeKey;
    private String attributeLabel;
    private String handler;
    @JSONField(serialize = false)
    private String data;
    private Object dataObj;

    public Long getId() {
        if (id == null) {
            id = SnowflakeUtil.uniqueLong();
        }
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFormUuid() {
        return formUuid;
    }

    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    //    private Integer isMultiple;
    public String getAttributeUuid() {
        return attributeUuid;
    }

    public void setAttributeUuid(String attributeUuid) {
        this.attributeUuid = attributeUuid;
    }

    public String getAttributeKey() {
        return attributeKey;
    }

    public void setAttributeKey(String attributeKey) {
        this.attributeKey = attributeKey;
    }

    public String getAttributeLabel() {
        return attributeLabel;
    }

    public void setAttributeLabel(String attributeLabel) {
        this.attributeLabel = attributeLabel;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getData() {
        if (dataObj != null) {
            if (dataObj instanceof JSONObject || dataObj instanceof JSONArray) {
                data = JSON.toJSONString(dataObj);
            } else if (dataObj instanceof String) {
                data = ((String) dataObj);
            } else {
                data = dataObj.toString();
            }
        }
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Object getDataObj() {
        if (dataObj != null) {
            if (StringUtils.isBlank(handler)) {
                return dataObj;
            }
            IFormAttributeHandler formAttributeHandler = FormAttributeHandlerFactory.getHandler(handler);
            if (formAttributeHandler == null) {
                return dataObj;
            }
            return formAttributeHandler.conversionDataType(dataObj, attributeLabel);
        } else {
            if (data == null) {
                return null;
            }
            if (StringUtils.isBlank(handler)) {
                return data;
            }
            IFormAttributeHandler formAttributeHandler = FormAttributeHandlerFactory.getHandler(handler);
            if (formAttributeHandler == null) {
                return data;
            }
            return formAttributeHandler.conversionDataType(data, attributeLabel);
        }
    }

    public void setDataObj(Object dataObj) {
        this.dataObj = dataObj;
    }

//    public Integer getIsMultiple() {
//        return isMultiple;
//    }
//
//    public void setIsMultiple(Integer isMultiple) {
//        this.isMultiple = isMultiple;
//    }

    public boolean dataIsEmpty() {
        Object dataObj = getDataObj();
        if (dataObj == null) {
            return true;
        } else if (dataObj instanceof JSONArray) {
            if (CollectionUtils.isEmpty((JSONArray) dataObj)) {
                return true;
            }
        } else if (dataObj instanceof JSONObject) {
            if (MapUtils.isEmpty((JSONObject) dataObj)) {
                return true;
            }
        } else if (dataObj instanceof String) {
            if (StringUtils.isBlank((String) dataObj)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attributeUuid == null) ? 0 : attributeUuid.hashCode());
        dataObj = getDataObj();
        result = prime * result + ((dataObj == null) ? 0 : dataObj.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AttributeDataVo other = (AttributeDataVo) obj;
        if (attributeUuid == null) {
            if (other.attributeUuid != null) {
                return false;
            }
        } else if (!attributeUuid.equals(other.attributeUuid)) {
            return false;
        }
        dataObj = getDataObj();
        Object otherDataObj = other.getDataObj();
        if (dataObj == null) {
            return otherDataObj == null;
        } else {
            return dataObj.equals(otherDataObj);
        }
    }

    @Override
    public int compareTo(AttributeDataVo attributeData) {
        return this.id.compareTo(attributeData.getId());
    }
}
