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
import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;

import java.util.List;

public enum ApiInvokedStatus implements IEnum {
    SUCCEED("succeed", new I18n("成功")),
    FAILED("failed", new I18n("失败"));
    private final String value;
    private final I18n text;

    ApiInvokedStatus(String value, I18n text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return $.t(text.toString());
    }

    @Override
    public List getValueTextList() {
        JSONArray array = new JSONArray();
        for (ApiInvokedStatus type : values()) {
            array.add(new JSONObject() {
                {
                    this.put("value", type.getValue());
                    this.put("text", type.getText());
                }
            });
        }
        return array;
    }

    public static String getApiInvokedStatusText(String value) {
        for (ApiInvokedStatus status : ApiInvokedStatus.values()) {
            if (status.getValue().equals(value)) {
                return status.getText();
            }
        }
        return null;
    }
}