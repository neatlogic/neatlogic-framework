/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
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
