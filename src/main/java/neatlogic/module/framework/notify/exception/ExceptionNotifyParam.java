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
