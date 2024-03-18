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
