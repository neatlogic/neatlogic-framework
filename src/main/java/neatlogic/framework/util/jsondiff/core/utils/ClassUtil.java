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

package neatlogic.framework.util.jsondiff.core.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.util.jsondiff.common.exception.JsonDiffException;
import neatlogic.framework.util.jsondiff.common.model.Constant;


public class ClassUtil {

    public static boolean isSameClass(Object obj1, Object obj2) {
        if (obj1 != null && obj2 != null) {
            return obj1.getClass().equals(obj2.getClass());
        }
        return obj1 == null && obj2 == null;
    }

    /**
     * 判断当前对象是否为json数据格式中的基本类型
     *
     * @param obj 判断的对象
     * @return 是否为基本类型
     */
    public static boolean isPrimitiveType(Object obj) {
        if (obj == null) {
            return true;
        }

        if (obj instanceof JSONArray || obj instanceof JSONObject) {
            return false;
        }

        if (String.class.isAssignableFrom(obj.getClass())) {
            return true;
        }
        if (obj instanceof Number) {
            return true;
        }
        try {
            return ((Class<?>) obj.getClass().getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 获取className
     *
     * @param obj
     * @return
     */
    public static String getClassName(Object obj) {
        if (obj == null) {
            return Constant.NULL;
        }
        return obj.getClass().getName();
    }

    public static <T> T getClassNameInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new JsonDiffException(String.format("无法实例化: %s", clazz), e);
        }
    }
}
