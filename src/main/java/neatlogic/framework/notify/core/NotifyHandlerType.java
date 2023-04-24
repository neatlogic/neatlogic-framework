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

package neatlogic.framework.notify.core;

import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;

public enum NotifyHandlerType {

    EMAIL("email", new I18n("enum.framework.notifyhandlertype.email")),
    MESSAGE("message", new I18n("enum.framework.notifyhandlertype.message")),
    WECHAT("wechat", new I18n("enum.framework.notifyhandlertype.wechat"));

    private final String value;
    private final I18n text;

    NotifyHandlerType(String value, I18n text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return I18nUtils.getMessage(text.toString());
    }

    public static String getText(String _value) {
        for (NotifyHandlerType n : NotifyHandlerType.values()) {
            if (n.getValue().equals(_value)) {
                return n.getText();
            }
        }
        return "";
    }
}
