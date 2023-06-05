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

import neatlogic.framework.util.$;
import neatlogic.framework.util.I18nUtils;

public enum ApiParamType {
    INTEGER("int", $.t("common.int")),
    ENUM("enum", $.t("枚举型")),
    BOOLEAN("boolean", $.t("布尔型")),
    STRING("string", $.t("字符型")),
    LONG("long", $.t("长整型")),
    JSONOBJECT("jsonObject", $.t("json对象")),
    JSONARRAY("jsonArray", $.t("json数组")),
    IP("ip", $.t("ip")),
    EMAIL("email", $.t("邮箱")),
    REGEX("regex", $.t("正则表达式")),
    DOUBLE("double", $.t("双精度浮点数")),
    NOAUTH("noAuth", $.t("任意对象，无需校验")),
    FILE("file", $.t("附件"));

    private String name;
    private String text;

    private ApiParamType(String _name, String _text) {
        this.name = _name;
        this.text = _text;
    }

    public String getValue() {
        return name;
    }

    public String getText() {
        return I18nUtils.getMessage(text);
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
