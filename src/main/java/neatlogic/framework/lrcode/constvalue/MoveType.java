package neatlogic.framework.lrcode.constvalue;

import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;

/**
 * @Title: MoveType
 * @Package neatlogic.framework.lrcode.constvalue
 * @Description: 移动类型
 * @Author: linbq
 * @Date: 2021/3/17 17:24
 * Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
public enum MoveType {
    INNER("inner", new I18n("enum.framework.movetype.inner")),
    PREV("prev", new I18n("enum.framework.movetype.prev")),
    NEXT("next", new I18n("enum.framework.movetype.next"));
    private String value;
    private I18n text;

    MoveType(String value, I18n text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return I18nUtils.getMessage(text.toString());
    }

    public void setText(String text) {
        this.text = new I18n(text);
    }

    public static MoveType getMoveType(String _value) {
        for (MoveType e : values()) {
            if (e.getValue().equals(_value)) {
                return e;
            }
        }
        return null;
    }
}
