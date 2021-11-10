package codedriver.framework.matrix.constvalue;

import codedriver.framework.common.constvalue.IEnum;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public enum MatrixType implements IEnum {
    CUSTOM("custom", "自定义数据源", "custom"),
    EXTERNAL("external", "外部数据源", "integrationUuid"),
    VIEW("view", "视图", "fileId");

    private String value;
    private String name;
    private String key;
    MatrixType(String _value, String _name, String _key) {
        this.value = _value;
        this.name = _name;
        this.key = _key;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public static String getValue(String _value) {
        for (MatrixType s : MatrixType.values()) {
            if (s.getValue().equals(_value)) {
                return s.getValue();
            }
        }
        return null;
    }

    public static String getName(String _value) {
        for (MatrixType s : MatrixType.values()) {
            if (s.getValue().equals(_value)) {
                return s.getName();
            }
        }
        return "";
    }


    @Override
    public List getValueTextList() {
        JSONArray array = new JSONArray();
        for(MatrixType type : MatrixType.values()){
            array.add(new JSONObject(){
                {
                    this.put("value",type.getValue());
                    this.put("text",type.getName());
                    this.put("key",type.getKey());
                }
            });
        }
        return array;
    }
}
