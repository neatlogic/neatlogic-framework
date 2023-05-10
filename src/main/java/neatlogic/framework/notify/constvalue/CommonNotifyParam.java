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

package neatlogic.framework.notify.constvalue;

import neatlogic.framework.common.constvalue.ParamType;
import neatlogic.framework.notify.core.INotifyParam;
import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;

/**
 * @author linbq
 * @since 2021/10/18 11:57
 **/
public enum CommonNotifyParam implements INotifyParam {
    OPERATOR("operator", new I18n("enum.framework.commonnotifyparam.operator"), ParamType.STRING),
    CURRENT_TIME("currentTime", new I18n("enum.framework.commonnotifyparam.currenttime"), ParamType.STRING),
    NOTIFYTRIGGERTYPE("notifyTriggerType", new I18n("enum.framework.commonnotifyparam.notifytriggertype"), ParamType.STRING),
    HOMEURL("homeUrl", new I18n("enum.framework.commonnotifyparam.homeurl"), ParamType.STRING, "<a href=\"${DATA.homeUrl}\" target=\"_blank\"></a>"),
    ;

    private final String value;
    private final I18n text;
    private final ParamType paramType;
    private String freemarkerTemplate;

    CommonNotifyParam(String value, I18n text, ParamType paramType) {
        this(value, text, paramType, null);
    }

    CommonNotifyParam(String value, I18n text, ParamType paramType, String freemarkerTemplate) {
        this.value = value;
        this.text = text;
        this.paramType = paramType;
        this.freemarkerTemplate = freemarkerTemplate;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getText() {
        return text.toString();
    }

    @Override
    public ParamType getParamType() {
        return paramType;
    }

    @Override
    public String getFreemarkerTemplate() {
        return freemarkerTemplate;
    }
}
