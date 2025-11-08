/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package neatlogic.framework.restful.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.IEnum;
import neatlogic.framework.restful.auth.core.ApiAuthFactory;
import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;

import java.util.List;

public enum PublicApiAuthType implements IEnum {
    BASIC("basic", new I18n("Basic认证"));

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
        return $.t(text.toString());
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