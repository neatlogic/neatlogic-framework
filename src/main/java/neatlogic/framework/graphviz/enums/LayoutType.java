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

package neatlogic.framework.graphviz.enums;

import neatlogic.framework.common.constvalue.IEnum;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public enum LayoutType implements IEnum {
    DOT("dot", "分层布局", true),
    CIRCO("circo", "环形布局", false),
    NEATO("neato", "张力布局", false),
    OSAGE("osage", "阵列布局", false),
    TWOPI("twopi", "星形布局", false),
    FDP("fdp", "无向布局", false),
    SFDP("sfdp", "无向布局2", false),
    PATCHWORK("patchwork", "无向布局2", false);


    private final String value;
    private final String text;
    private final Boolean supportLayer;

    LayoutType(String _value, String _text, boolean _supportLayer) {
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
        return text;
    }

    public boolean getSupportLayer() {
        return supportLayer;
    }

}
