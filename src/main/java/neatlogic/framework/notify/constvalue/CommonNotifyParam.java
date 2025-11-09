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

package neatlogic.framework.notify.constvalue;

import neatlogic.framework.common.constvalue.ParamType;
import neatlogic.framework.notify.core.INotifyParam;
import neatlogic.framework.util.I18n;

/**
 * @author linbq
 * @since 2021/10/18 11:57
 **/
public enum CommonNotifyParam implements INotifyParam {
    OPERATOR("operator", new I18n("操作人"), ParamType.STRING),
    CURRENT_TIME("currentTime", new I18n("当前时间"), ParamType.STRING),
    NOTIFYTRIGGERTYPE("notifyTriggerType", new I18n("通知触发点"), ParamType.STRING),
    HOMEURL("homeUrl", new I18n("域名"), ParamType.STRING, "<a href=\"${DATA.homeUrl}\" target=\"_blank\"></a>"),
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
