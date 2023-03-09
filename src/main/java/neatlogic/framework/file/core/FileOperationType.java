package neatlogic.framework.file.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.IEnum;

import java.util.List;

/**
 * @author longrf
 * @date 2023/3/6 10:34
 */

public enum FileOperationType implements IEnum {
    DELETE("delete", "enum.framework.fileoperationtype.delete"),
    DOWNLOAD("download", "enum.framework.fileoperationtype.download");
    private String value;
    private String text;

    FileOperationType(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    @Override
    public List getValueTextList() {
        JSONArray array = new JSONArray();
        for (FileOperationType type : values()) {
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
