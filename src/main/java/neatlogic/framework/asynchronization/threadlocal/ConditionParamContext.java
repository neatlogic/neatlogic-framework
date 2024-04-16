/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.asynchronization.threadlocal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

public class ConditionParamContext {

    private static final ThreadLocal<ConditionParamContext> instance = new ThreadLocal<>();
    /**
     * 参数数据
     **/
    private JSONObject paramData = new JSONObject();
    /**
     * 表单配置信息
     **/
    private JSONObject formConfig;
    /**
     * 是否需要将参数名称、表达式、值的value翻译成对应text，目前条件步骤生成活动时用到
     **/
    private boolean translate = false;

    public static ConditionParamContext init(JSONObject _paramData) {
        ConditionParamContext context = new ConditionParamContext();
        if (MapUtils.isNotEmpty(_paramData)) {
            context.paramData.putAll(_paramData);
            String formConfigStr = _paramData.getString("formConfig");
            if (StringUtils.isNotBlank(formConfigStr)) {
                context.formConfig = JSON.parseObject(formConfigStr);
            }
        }
        instance.set(context);
        return context;
    }

    public static ConditionParamContext get() {
        return instance.get();
    }

    public void release() {
        instance.remove();
    }

    private ConditionParamContext() {

    }

    public JSONObject getParamData() {
        return paramData;
    }

    public ConditionParamContext setParamData(JSONObject paramData) {
        this.paramData = paramData;
        return this;
    }

    public JSONObject getFormConfig() {
        return formConfig;
    }

    public ConditionParamContext setFormConfig(JSONObject formConfig) {
        this.formConfig = formConfig;
        return this;
    }

    public boolean isTranslate() {
        return translate;
    }

    public ConditionParamContext setTranslate(boolean translate) {
        this.translate = translate;
        return this;
    }
}
