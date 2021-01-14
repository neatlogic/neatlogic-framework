package codedriver.framework.restful.constvalue;

import codedriver.framework.common.constvalue.IEnum;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public enum OperationTypeEnum implements IEnum {
    CREATE("create","增加"),
    DELETE("delete","删除"),
    UPDATE("update","更新"),
    SEARCH("search","查询"),
    OPERATE("operate","操作");
    private String name;
    private String text;

    private OperationTypeEnum(String _value, String _text){
        this.name = _value;
        this.text = _text;
    }

    public String getValue(){
        return name;
    }

    public String getText(){
        return text;
    }

    public static String getText(String value){
        for (OperationTypeEnum f : OperationTypeEnum.values()){
            if (f.getValue().equals(value)){
                return f.getText();
            }
        }
        return "";
    }


    @Override
    public List getValueTextList() {
        JSONArray array = new JSONArray();
        for(OperationTypeEnum typeEnum : OperationTypeEnum.values()){
            array.add(new JSONObject(){
                {
                    this.put("value",typeEnum.getValue());
                    this.put("text",typeEnum.getText());
                }
            });
        }
        return array;
    }
}
