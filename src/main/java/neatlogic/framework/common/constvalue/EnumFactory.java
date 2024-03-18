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
