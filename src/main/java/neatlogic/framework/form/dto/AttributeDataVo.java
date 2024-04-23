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

package neatlogic.framework.form.dto;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.form.attribute.core.FormAttributeHandlerFactory;
import neatlogic.framework.form.attribute.core.IFormAttributeHandler;
import neatlogic.framework.util.SnowflakeUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

public class AttributeDataVo implements Comparable<AttributeDataVo> {
    private Long id;
    private String formUuid;
    private String tag;
    private String attributeUuid;
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
