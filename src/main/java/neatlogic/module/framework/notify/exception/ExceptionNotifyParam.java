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

package neatlogic.module.framework.notify.exception;

import neatlogic.framework.common.constvalue.ParamType;
import neatlogic.framework.notify.core.INotifyParam;
import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;

/**
 * @author laiwt
 * @since 2021/10/21 13:49
 **/
public enum ExceptionNotifyParam implements INotifyParam {

    EXCEPTIONSTACK("exceptionstack", new I18n("异常信息"), ParamType.STRING),
    EXCEPTIONCALLER("exceptioncaller", new I18n("发起方信息"), ParamType.STRING);

    private final String value;
    private final I18n text;
    private final ParamType paramType;

    ExceptionNotifyParam(String value, I18n text, ParamType paramType) {
        this.value = value;
        this.text = text;
        this.paramType = paramType;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getText() {
        return $.t(text.toString());
    }

    @Override
    public ParamType getParamType() {
        return paramType;
    }
}
