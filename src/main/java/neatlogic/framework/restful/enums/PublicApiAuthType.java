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

import neatlogic.framework.common.constvalue.IEnum;
import neatlogic.framework.restful.auth.core.ApiAuthFactory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;

import java.util.List;

public enum PublicApiAuthType implements IEnum {
    BASIC("basic", new I18n("common.basicauth"));

    private final String type;
    private final I18n text;

    PublicApiAuthType(String _type, I18n _text) {
        this.type = _type;
        this.text = _text;
    }

    public String getValue() {
        return this.type;
    }

    public String getText() {
        return I18nUtils.getMessage(text.toString());
    }

    public static String getText(String value) {
        for (PublicApiAuthType type : PublicApiAuthType.values()) {
            if (type.getValue().equals(value)) {
                return type.getText();
            }
        }
        return "";
    }

    public static PublicApiAuthType getAuthenticateType(String value) {
        for (PublicApiAuthType type : PublicApiAuthType.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }


    @Override
    public List getValueTextList() {
        JSONArray array = new JSONArray();
        for (PublicApiAuthType type : PublicApiAuthType.values()) {
            array.add(new JSONObject() {
                {
                    this.put("value", type.getValue());
                    this.put("text", type.getText());
                    this.put("help", ApiAuthFactory.getApiAuth(type.getValue()).help());
                }
            });
        }
        return array;
    }
}