/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
