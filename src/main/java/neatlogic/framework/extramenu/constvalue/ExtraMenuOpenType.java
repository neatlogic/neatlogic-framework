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

package neatlogic.framework.extramenu.constvalue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.IEnum;

import java.util.List;

public enum ExtraMenuOpenType implements IEnum {
    WINDOW("window", "新窗口"), IFRAME("iframe", "嵌套页面");

    private final String value;
    private final String text;

    ExtraMenuOpenType(String value, String text) {
        this.value = value;
        this.text = text;
    }

    @Override
    public List getValueTextList() {
        JSONArray array = new JSONArray();
        for (ExtraMenuOpenType typeEnum : ExtraMenuOpenType.values()) {
            array.add(new JSONObject() {
                {
                    this.put("value", typeEnum.getValue());
                    this.put("text", typeEnum.getText());
                }
            });
        }
        return array;
    }

    public static String getText(String value) {
        for (ExtraMenuOpenType s : ExtraMenuOpenType.values()) {
            if (s.getValue().equals(value)) {
                return s.getText();
            }
        }
        return "";
    }

    @Override
    public String getValue() {
        return value;
    }


    public String getText() {
        return text;
    }

}
