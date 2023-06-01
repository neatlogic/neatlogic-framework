/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.common.constvalue;

import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;

public enum ApiParamType {
    INTEGER("int", new I18n("整型")),
    ENUM("enum", new I18n("枚举型")),
    BOOLEAN("boolean", new I18n("布尔型")),
    STRING("string", new I18n("字符型")),
    LONG("long", new I18n("长整型")),
    JSONOBJECT("jsonObject", new I18n("json对象")),
    JSONARRAY("jsonArray", new I18n("json数组")),
    IP("ip", new I18n("ip")),
    EMAIL("email", new I18n("邮箱")),
    REGEX("regex", new I18n("正则表达式")),
    DOUBLE("double", new I18n("双精度浮点数")),
    NOAUTH("noAuth", new I18n("任意对象，无需校验")),
    FILE("file", new I18n("附件"));

    private String name;
    private I18n text;

    private ApiParamType(String _name, I18n _text) {
        this.name = _name;
        this.text = _text;
    }

    public String getValue() {
        return name;
    }

    public String getText() {
        return I18nUtils.getMessage(text.toString());
    }

    public static String getText(String name) {
        for (ApiParamType s : ApiParamType.values()) {
            if (s.getValue().equals(name)) {
                return s.getText();
            }
        }
        return "";
    }

    public static ApiParamType getApiParamType(String name) {
        for (ApiParamType s : ApiParamType.values()) {
            if (s.getValue().equals(name)) {
                return s;
            }
        }
        return NOAUTH;
    }
}
