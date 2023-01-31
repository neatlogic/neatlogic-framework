/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.dependency.core;

import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;

/**
 * 被引用者（上游）类型工厂
 *
 * @author: linbq
 * @since: 2021/4/2 11:37
 **/
public class FromTypeFactory {
    /**
     * 标记是否未初始化数据，只初始化一次
     **/
    private static volatile boolean isUninitialized = true;

    private static Set<IFromType> set = new HashSet<>();

    private static String allCalleeTypeToString;

    /**
     * 获取IFromType接口所有实现枚举类集合
     *
     * @return
     */
    public static Set<IFromType> getCalleeTypeSet() {
        if (isUninitialized) {
            synchronized (FromTypeFactory.class) {
                if (isUninitialized) {
                    Reflections reflections = new Reflections("neatlogic");
                    Set<Class<? extends IFromType>> classSet = reflections.getSubTypesOf(IFromType.class);
                    for (Class<? extends IFromType> c : classSet) {
                        for (IFromType obj : c.getEnumConstants()) {
                            set.add(obj);
                        }
                    }
                    isUninitialized = false;
                }
            }
        }
        return set;
    }

    /**
     * 通过_value值查询对应的枚举类
     *
     * @param _value
     * @return
     */
    public static IFromType getCalleeType(String _value) {
        for (IFromType type : getCalleeTypeSet()) {
            if (type.getValue().equals(_value)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 用于用户传入错误数据访问接口时，提示fromType参数的所有可选值
     *
     * @return 返回IFromType接口所有实现枚举类集合的数据信息
     */
    public static String getAllCalleeTypeToString() {
        if (StringUtils.isBlank(allCalleeTypeToString)) {
            StringBuilder stringBuilder = new StringBuilder();
            for (IFromType type : getCalleeTypeSet()) {
                stringBuilder.append(type.getValue());
                stringBuilder.append("（");
                stringBuilder.append(type.getText());
                stringBuilder.append("），");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            allCalleeTypeToString = stringBuilder.toString();
        }
        return allCalleeTypeToString;
    }
}
