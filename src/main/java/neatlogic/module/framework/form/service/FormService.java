/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

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

package neatlogic.module.framework.form.service;

import com.alibaba.fastjson.JSONArray;
import neatlogic.framework.form.dto.AttributeDataVo;
import neatlogic.framework.form.dto.FormAttributeVo;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.form.dto.FormVersionVo;
import neatlogic.framework.form.exception.AttributeValidException;

public interface FormService {
    /**
     * 保存表单属性与其他功能的引用关系
     * @param formAttributeVo
     */
    void saveDependency(FormAttributeVo formAttributeVo);

    /**
     * 表格输入组件密码类型加密
     * @param data
     */
    JSONArray staticListPasswordEncrypt(JSONArray data, JSONObject config);

    Object textConversionValueForSelectHandler(Object text, JSONObject config);

    /**
     * 校验表单数据有效性，并针对特殊组件数据进行相应处理，如密码类型组件对数据进行加密处理
     * @param formVersionVo
     * @param formAttributeDataList
     * @throws AttributeValidException
     */
    void formAttributeValueValid(FormVersionVo formVersionVo, JSONArray formAttributeDataList) throws AttributeValidException;

    JSONObject getMyDetailedDataForSelectHandler(AttributeDataVo attributeDataVo, JSONObject configObj);
}
