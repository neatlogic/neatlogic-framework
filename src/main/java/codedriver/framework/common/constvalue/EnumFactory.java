package codedriver.framework.common.constvalue;

import org.reflections.Reflections;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @Title: EnumFactory
 * @Package: codedriver.framework.common.constvalue
 * @Description: 枚举工厂，将实现了IEnum接口的枚举纳入工厂
 * @Author: laiwt
 * @Date: 2021/1/11 19:03
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class EnumFactory{
    private static Map<String, List> enumMap = new HashMap<>();

    static {
        Reflections reflections = new Reflections("codedriver");
        Set<Class<? extends IEnum>> enumClasses = reflections.getSubTypesOf(IEnum.class);
        for (Class<? extends IEnum> c: enumClasses) {
            try {
                Object[] objects = c.getEnumConstants();
                List valueTextList = (List) c.getMethod("getValueTextList").invoke(objects[0]);
                enumMap.put(c.getName(), valueTextList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @Description: 通过枚举类的完整类名获取其枚举值
     * @Author: laiwt
     * @Date: 2021/1/12 11:29
     * @Params: [enumClass]
     * @Returns: com.alibaba.fastjson.JSONArray
    **/
    public static List getValueTextList(String enumClass) {
        return enumMap.get(enumClass);
    }

}
