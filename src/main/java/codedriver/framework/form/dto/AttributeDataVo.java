/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.form.dto;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class AttributeDataVo {
    private String attributeUuid;
    private String data;
    private Object dataObj;
    private Integer isMultiple;
    public String getAttributeUuid() {
        return attributeUuid;
    }

    public void setAttributeUuid(String attributeUuid) {
        this.attributeUuid = attributeUuid;
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
        result = prime * result + ((data == null) ? 0 : data.hashCode());
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
        if (data == null) {
            return other.data == null;
        } else {
            return data.equals(other.data);
        }
    }
}
