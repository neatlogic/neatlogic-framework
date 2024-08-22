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
import neatlogic.framework.form.attribute.core.FormHandlerBase;
import neatlogic.framework.form.constvalue.FormConditionModel;
import neatlogic.framework.form.constvalue.FormHandler;
import neatlogic.framework.form.dto.AttributeDataVo;
import neatlogic.framework.form.exception.AttributeValidException;
import neatlogic.framework.form.exception.FormExtendAttributeConfigIllegalException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class LinkHandler extends FormHandlerBase {

    @Override
    public String getHandler() {
        return FormHandler.FORMLINK.getHandler();
    }

    @Override
    public JSONObject valid(AttributeDataVo attributeDataVo, JSONObject configObj) throws AttributeValidException {
        return null;
    }

    @Override
    public int getSort() {
        return 9;
    }

    @Override
    public Object valueConversionText(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return null;
    }

    @Override
    public Object dataTransformationForEmail(AttributeDataVo attributeDataVo, JSONObject configObj) {
        String value = configObj.getString("value");
        String text = configObj.getString("text");
        String target = configObj.getString("target");
        return "<a href='" + value + "' target='" + target + "'>" + text + "</a>";
    }

    @Override
    public Object textConversionValue(Object text, JSONObject config) {
        return null;
    }

    @Override
    public String getHandlerName() {
        return FormHandler.FORMLINK.getHandlerName();
    }

    @Override
    public String getIcon() {
        return "tsfont-formlink";
    }

    @Override
    public ParamType getParamType() {
        return ParamType.STRING;
    }

    @Override
    public String getDataType() {
        return "string";
    }

    @Override
    public boolean isConditionable() {
        return false;
    }

    @Override
    public boolean isShowable() {
        return true;
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
    public String getModule() {
        return "framework";
    }

    @Override
    public boolean isForTemplate() {
        return false;
    }

    @Override
    public boolean isProcessTaskBatchSubmissionTemplateParam() {
        return false;
    }

    @Override
    public boolean isAudit() {
        return false;
    }

    @Override
    public String getHandlerType(FormConditionModel model) {
        return null;
    }

    /*
    表单组件配置信息
    {
        "handler": "formlink",
        "reaction": {
            "hide": {},
            "readonly": {},
            "display": {}
        },
        "override_config": {},
        "icon": "tsfont-formlink",
        "hasValue": false,
        "label": "超链接_7",
        "type": "form",
        "category": "basic",
        "config": {
            "isMask": false,
            "width": "100%",
            "description": "",
            "text": "百度",
            "value": "https://www.baidu.com/",
            "isHide": false,
            "target": "_blank"
        },
        "uuid": "d4fc474411bd448f8ea5ce384032efe4"
    }
     */
    /*
    保存数据结构
    无
     */
    /*
    返回数据结构
    {
        "text": "百度",
        "label": "<a href='https://www.baidu.com/' target='_blank'>百度</a>",
        "value": "https://www.baidu.com/"
    }
     */
    @Override
    protected JSONObject getMyDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj) {
        String value = configObj.getString("value");
        String text = configObj.getString("text");
        String target = configObj.getString("target");
        JSONObject resultObj = new JSONObject();
        resultObj.put("value", value);
        resultObj.put("text", text);
        resultObj.put("label", "<a href='" + value + "' target='" + target + "'>" + text + "</a>");
        return resultObj;
    }

    @Override
    public Object dataTransformationForExcel(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return configObj.getString("value");
    }

    @Override
    protected void myValidateExtendAttributeConfig(String key, JSONObject config) {
        String value = config.getString("value");
        if (value == null) {
            throw new FormExtendAttributeConfigIllegalException(this.getHandler(), key, "config.config.value");
        }
        if (StringUtils.isBlank(value)) {
            throw new FormExtendAttributeConfigIllegalException(this.getHandler(), key, "config.config.value", value);
        }
        String text = config.getString("text");
        if (text == null) {
            throw new FormExtendAttributeConfigIllegalException(this.getHandler(), key, "config.config.text");
        }
        if (StringUtils.isBlank(text)) {
            throw new FormExtendAttributeConfigIllegalException(this.getHandler(), key, "config.config.text", text);
        }
    }
}
