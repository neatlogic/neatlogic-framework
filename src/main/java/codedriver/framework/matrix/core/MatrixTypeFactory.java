/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.matrix.core;

import codedriver.framework.common.constvalue.IEnum;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * @author linbq
 * @since 2021/11/16 15:24
 **/
public class MatrixTypeFactory implements IEnum {
    private static volatile boolean isUninitialed = true;

    private static List<IMatrixType> list = new ArrayList<>();

    public static List<IMatrixType> getMatrixTypeList() {
        if (isUninitialed) {
            synchronized (MatrixTypeFactory.class) {
                if (isUninitialed) {
                    Reflections reflections = new Reflections("codedriver");
                    Set<Class<? extends IMatrixType>> classSet = reflections.getSubTypesOf(IMatrixType.class);
                    for (Class<? extends IMatrixType> c : classSet) {
                        for (IMatrixType obj : c.getEnumConstants()) {
                            list.add(obj);
                        }
                    }
                    list.sort(Comparator.comparingInt(IMatrixType::getSort));
                    isUninitialed = false;
                }
            }
        }
        return list;
    }

    public static String getName(String _value) {
        for (IMatrixType s : getMatrixTypeList()) {
            if (s.getValue().equals(_value)) {
                return s.getName();
            }
        }
        return "";
    }

    @Override
    public List getValueTextList() {
        JSONArray array = new JSONArray();
        for(IMatrixType type : getMatrixTypeList()){
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
