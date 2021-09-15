/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.common.constvalue;

import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class EnumFactory{
    private static final Map<String, Class<? extends IEnum>> enumMap = new HashMap<>();

    static {
        Reflections reflections = new Reflections("codedriver");
        Set<Class<? extends IEnum>> enumClasses = reflections.getSubTypesOf(IEnum.class);
        for (Class<? extends IEnum> c: enumClasses) {
            try {
                enumMap.put(c.getName(), c);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @Description: 通过枚举的全类名获取枚举类
     * @Author: laiwt
     * @Date: 2021/1/12 19:10
     * @Params: [enumClass]
     * @Returns: java.lang.Class<? extends codedriver.framework.common.constvalue.IEnum>
    **/
    public static Class<? extends IEnum> getEnumClass(String enumClass) {
        return enumMap.get(enumClass);
    }

}
