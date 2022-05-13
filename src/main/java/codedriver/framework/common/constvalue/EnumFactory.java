/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.common.constvalue;

import codedriver.framework.common.dto.ValueTextVo;
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
        Reflections reflections = new Reflections("codedriver");
        Set<Class<? extends IEnum>> enumClasses = reflections.getSubTypesOf(IEnum.class);
        for (Class<? extends IEnum> c : enumClasses) {
            //处理所有枚举
            try {
                enumMap.put(c.getName(), c);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            //处理具名枚举
            try {
                Object instance = null;
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
