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

package neatlogic.module.framework.form.attribute.handler;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.ParamType;
import neatlogic.framework.common.util.RC4Util;
import neatlogic.framework.form.attribute.core.FormHandlerBase;
import neatlogic.framework.form.constvalue.FormConditionModel;
import neatlogic.framework.form.constvalue.FormHandler;
import neatlogic.framework.form.dto.AttributeDataVo;
import neatlogic.framework.form.exception.AttributeValidException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class PasswordHandler extends FormHandlerBase {

    @Override
    public String getHandler() {
        return FormHandler.FORMPASSWORD.getHandler();
    }

    @Override
    public String getHandlerType(FormConditionModel model) {
        return "input";
    }

    @Override
    public JSONObject valid(AttributeDataVo attributeDataVo, JSONObject configObj) throws AttributeValidException {
        String dataObj = (String) attributeDataVo.getDataObj();
        if (StringUtils.isNotBlank(dataObj)) {
            // 加密
            attributeDataVo.setDataObj(RC4Util.encrypt(dataObj));
        }
        return null;
    }

    @Override
    public Object conversionDataType(Object source, String attributeLabel) {
        if (source == null) {
            return null;
        }
        if (source instanceof String) {
            return RC4Util.encrypt((String) source);
        }
        throw new AttributeValidException(attributeLabel);
    }

    @Override
    public Object valueConversionText(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return "已更新";
    }

    @Override
    public Object dataTransformationForEmail(AttributeDataVo attributeDataVo, JSONObject configObj) {
        Object dataObj = attributeDataVo.getDataObj();
        if (dataObj != null) {
            String data = (String) dataObj;
            if (StringUtils.isNotBlank(data)) {
                return StringUtils.repeat("*", data.length());
            }
        }
        return dataObj;
    }

    @Override
    public Object textConversionValue(Object text, JSONObject config) {
        return text;
    }

    @Override
    public ParamType getParamType() {
        return ParamType.STRING;
    }

    @Override
    public boolean isConditionable() {
        return true;
    }

    @Override
    public boolean isProcessTaskBatchSubmissionTemplateParam() {
        return true;
    }

    @Override
    public boolean isAudit() {
        return true;
    }

    /*
    表单组件配置信息
    {
        "handler": "formpassword",
        "reaction": {
            "hide": {},
            "readonly": {},
            "disable": {},
            "display": {},
            "mask": {}
        },
        "override_config": {},
        "icon": "tsfont-option-horizontal",
        "hasValue": true,
        "label": "密码_4",
        "type": "form",
        "category": "basic",
        "config": {
            "isRequired": false,
            "isMask": false,
            "width": "100%",
            "description": "",
            "isHide": false
        },
        "uuid": "4c0f58dc583c4fb0a3e5ff3e10b75797",
        "switchHandler": [
            "formtext",
            "formtextarea",
            "formckeditor",
            "formnumber",
            "formpassword"
        ]
    }
     */
    /*
    保存数据结构
    "12345"
     */
    /*
    返回数据结构
    {
        "value": "12345"
    }
     */
    @Override
    protected JSONObject getMyDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = new JSONObject();
        resultObj.put("value", attributeDataVo.getDataObj());
        return resultObj;
    }

    @Override
    public Object dataTransformationForExcel(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return attributeDataVo.getDataObj();
    }
}
