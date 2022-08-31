/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.matrix.constvalue;

import codedriver.framework.common.constvalue.IEnum;
import codedriver.framework.matrix.core.IMatrixType;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public enum MatrixType implements IMatrixType, IEnum {
    CUSTOM("custom", "自定义数据源", "custom", 1),
    EXTERNAL("external", "外部数据源", "integrationUuid", 2),
    PRIVATE("private", "私有数据源", "private", 5),
    VIEW("view", "数据库视图", "fileId", 3);

    private String value;
    private String name;
    private String key;
    private int sort;

    MatrixType(String _value, String _name, String _key, int _sort) {
        this.value = _value;
        this.name = _name;
        this.key = _key;
        this.sort = _sort;
    }
    @Override
    public String getValue() {
        return value;
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public String getKey() {
        return key;
    }

    @Override
    public int getSort() {
        return sort;
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
