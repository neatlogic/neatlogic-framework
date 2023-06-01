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

package neatlogic.module.framework.notify.exception;

import neatlogic.framework.common.constvalue.ParamType;
import neatlogic.framework.notify.core.INotifyParam;
import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;

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
        return I18nUtils.getMessage(text.toString());
    }

    @Override
    public ParamType getParamType() {
        return paramType;
    }
}
