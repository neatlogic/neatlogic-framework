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

package neatlogic.framework.graphviz.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.IEnum;
import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;

import java.util.List;

public enum LayoutType implements IEnum {
    DOT("dot", new I18n("分层布局"), true),
    CIRCO("circo", new I18n("环形布局"), false),
    NEATO("neato", new I18n("张力布局"), false),
    OSAGE("osage", new I18n("阵列布局"), false),
    TWOPI("twopi", new I18n("星形布局"), false),
    FDP("fdp", new I18n("无向布局"), false),
    SFDP("sfdp", new I18n("无向布局2"), false),
    PATCHWORK("patchwork", new I18n("无向布局2"), false);


    private final String value;
    private final I18n text;
    private final Boolean supportLayer;

    LayoutType(String _value, I18n _text, boolean _supportLayer) {
        this.value = _value;
        this.text = _text;
        this.supportLayer = _supportLayer;
    }

    public static LayoutType get(String value) {
        for (LayoutType l : LayoutType.values()) {
            if (l.getValue().equalsIgnoreCase(value)) {
                return l;
            }
        }
        return null;
    }

    @Override
    public List getValueTextList() {
        JSONArray array = new JSONArray();
        for (LayoutType layoutType : LayoutType.values()) {
            array.add(new JSONObject() {
                {
                    this.put("value", layoutType.getValue());
                    this.put("text", layoutType.getText());
                }
            });
        }
        return array;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return $.t(text.toString());
    }

    public boolean getSupportLayer() {
        return supportLayer;
    }

}
