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
