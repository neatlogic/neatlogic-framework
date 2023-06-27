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

package neatlogic.framework.common.constvalue;

import neatlogic.framework.common.dto.ValueTextVo;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class EnumFactory {
    private static final Map<String, Class<? extends IEnum>> enumMap = new HashMap<>();
    private static final Map<String, String> namedEnumNameMap = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(EnumFactory.class);

    static {
        Reflections reflections = new Reflections("neatlogic");
        Set<Class<? extends IEnum>> enumClasses = reflections.getSubTypesOf(IEnum.class);
        for (Class<? extends IEnum> c : enumClasses) {
            //处理所有枚举
            try {
                enumMap.put(c.getName(), c);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            if (!c.isInterface()) {
                //处理具名枚举
                try {
                    Object instance;
                    Object[] objects = c.getEnumConstants();
                    if (objects != null && objects.length > 0) {
                        instance = objects[0];
                    } else {
                        instance = c.newInstance();
                    }
                    Object name = c.getMethod("getEnumName").invoke(instance);
                    if (name != null && StringUtils.isNotBlank(name.toString())) {
                        namedEnumNameMap.put(name.toString(), c.getName());
                    }
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                }
            } else {
                for (Class<?> cls : reflections.getSubTypesOf(c)) {
                    if (!cls.isInterface()) {
                        Object instance = null;
                        Object[] objects = cls.getEnumConstants();
                        if (objects != null && objects.length > 0) {
                            instance = objects[0];
                        } else {
                            try {
                                instance = cls.newInstance();
                            } catch (Exception ignored) {

                            }
                        }
                        if (instance != null) {
                            Object name = null;
                            try {
                                name = cls.getMethod("getEnumName").invoke(instance);
                            } catch (Exception ignored) {

                            }
                            if (name != null && StringUtils.isNotBlank(name.toString())) {
                                //随便找到一个实现类能返回名称就退出循环，正常情况相爱getEnumName应该写在接口的default方法里。
                                namedEnumNameMap.put(name.toString(), c.getName());
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @Description: 通过枚举的全类名获取枚举类
     * @Author: laiwt
     * @Date: 2021/1/12 19:10
     * @Params: [enumClass]
     * @Returns: java.lang.Class<? extends neatlogic.framework.common.constvalue.IEnum>
     **/
    public static Class<? extends IEnum> getEnumClass(String enumClass) {
        Class<? extends IEnum> value = enumMap.get(enumClass);
        if (value != null) {
            return value;
        }
        for (String key : enumMap.keySet()) {
            if (key.endsWith("." + enumClass)) {
                return enumMap.get(key);
            }
        }
        return null;
    }

    public static List<ValueTextVo> searchEnumClassByName(String name) {
        List<ValueTextVo> returnList = new ArrayList<>();
        for (String n : namedEnumNameMap.keySet()) {
            if (StringUtils.isBlank(name) || n.contains(name)) {
                ValueTextVo valueText = new ValueTextVo();
                valueText.setText(n);
                valueText.setValue(namedEnumNameMap.get(n));
                returnList.add(valueText);
            }
        }
        return returnList;
    }
}
