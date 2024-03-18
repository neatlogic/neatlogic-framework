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

package neatlogic.framework.dependency.core;

import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 被引用者（上游）类型工厂
 **/
public class FromTypeFactory {
    /**
     * 标记是否未初始化数据，只初始化一次
     **/
    private static volatile boolean isUninitialized = true;

    private static final Set<IFromType> set = new HashSet<>();

    private static String allCalleeTypeToString;

    /**
     * 获取IFromType接口所有实现枚举类集合
     *
     */
    public static Set<IFromType> getCalleeTypeSet() {
        if (isUninitialized) {
            synchronized (FromTypeFactory.class) {
                if (isUninitialized) {
                    Reflections reflections = new Reflections("neatlogic");
                    Set<Class<? extends IFromType>> classSet = reflections.getSubTypesOf(IFromType.class);
                    for (Class<? extends IFromType> c : classSet) {
                        Collections.addAll(set, c.getEnumConstants());
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
