/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.integration.authentication.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.IEnum;
import neatlogic.framework.util.$;

import java.util.List;

public enum AuthenticateType implements IEnum {
    NOAUTH("noauth", "common.noauth"),
    BUILDIN("buildin", "common.buildinauth"),

    BASIC("basicauth", "common.basicauth"),

    BEARER("bearertoken", "common.bearerauth");

    private final String type;
    private final String text;

    AuthenticateType(String _type, String _text) {
        this.type = _type;
        this.text = _text;
    }

    public String getValue() {
        return this.type;
    }

    public String getText() {
        return $.t(text);
    }

    public static String getText(String value) {
        for (AuthenticateType type : AuthenticateType.values()) {
            if (type.getValue().equals(value)) {
                return type.getText();
            }
        }
        return "";
    }


    @Override
    public List getValueTextList() {
        JSONArray array = new JSONArray();
        for (AuthenticateType type : AuthenticateType.values()) {
            array.add(new JSONObject() {
                {
                    this.put("value", type.getValue());
                    this.put("text", type.getText());
                }
            });
        }
        return array;
    }
}
