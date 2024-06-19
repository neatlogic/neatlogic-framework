/*Copyright (C) 2023  深圳极向量科技有限公司 All Rights Reserved.

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
import neatlogic.framework.form.dto.AttributeDataVo;
import neatlogic.framework.form.dto.FormAttributeVo;

import java.util.List;

public interface FormService {

    /**
     * 表单下拉框通过text值查找value值
     * @param text
     * @param config
     * @return
     */
    JSONArray textConversionValueForSelectHandler(Object text, JSONObject config);

    /**
     * 表单下拉框通过原始数据获取详细数据
     * @param attributeDataVo
     * @param configObj
     * @return
     */
    JSONObject getMyDetailedDataForSelectHandler(AttributeDataVo attributeDataVo, JSONObject configObj);

    /**
     * 获取表单组件列表
     * @param formUuid 表单UUID
     * @param formName 表单名
     * @param tag 标签
     * @return 组件列表
     */
    List<FormAttributeVo> getFormAttributeList(String formUuid, String formName, String tag);

    /**
     * 获取表单组件列表
     *
     * @param formUuid 表单UUID
     * @param formName 表单名
     * @param tag      标签
     * @return 组件列表
     */
    List<FormAttributeVo> getFormAttributeListNew(String formUuid, String formName, String tag);

    /**
     * 通过简单值获取标准值，例如下拉框的简单值为a或A，返回标准值为{"value":"a", "text":"A"}
     * @param simpleValue
     * @param configObj
     * @return
     */
    Object getSelectStandardValueBySimpleValue(Object simpleValue, JSONObject configObj);
}
