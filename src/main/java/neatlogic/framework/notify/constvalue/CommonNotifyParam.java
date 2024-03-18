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
