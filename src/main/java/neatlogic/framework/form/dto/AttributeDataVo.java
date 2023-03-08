/*
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.framework.form.dto;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class AttributeDataVo {
    private String attributeUuid;
    private String attributeLabel;
    @JSONField(serialize = false)
    private String data;
    private Object dataObj;
    private Integer isMultiple;
    public String getAttributeUuid() {
        return attributeUuid;
    }

    public void setAttributeUuid(String attributeUuid) {
        this.attributeUuid = attributeUuid;
    }

    public String getAttributeLabel() {
        return attributeLabel;
    }

    public void setAttributeLabel(String attributeLabel) {
        this.attributeLabel = attributeLabel;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Object getDataObj() {
        if (dataObj != null) {
            return dataObj;
        }
        if (data == null) {
            return null;
        }
        if (data.startsWith("[") && data.endsWith("]")) {
            return JSON.parseArray(data);
        } else if (data.startsWith("{") && data.endsWith("}")) {
            return JSON.parseObject(data);
        } else {
            try {
                return Integer.valueOf(data);
            } catch (NumberFormatException e) {
            }
            try {
                return Long.valueOf(data);
            } catch (NumberFormatException e) {
            }
            try {
                return Double.valueOf(data);
            } catch (NumberFormatException e) {
            }
            return data;
        }
    }

    public void setDataObj(Object dataObj) {
        this.dataObj = dataObj;
    }

    public Integer getIsMultiple() {
        return isMultiple;
    }

    public void setIsMultiple(Integer isMultiple) {
        this.isMultiple = isMultiple;
    }

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
}
