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

package neatlogic.framework.restful.enums;

import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;

public enum TreeMenuType {
    SYSTEM("system", new I18n("系统接口目录")),
    CUSTOM("custom", new I18n("自定义接口目录")),
    AUDIT("audit", new I18n("操作审计目录"));

    private final String name;
    private final I18n text;

    TreeMenuType(String _name, I18n _text) {
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
        for (TreeMenuType s : TreeMenuType.values()) {
            if (s.getValue().equals(name)) {
                return s.getText();
            }
        }
        return "";
    }
}
