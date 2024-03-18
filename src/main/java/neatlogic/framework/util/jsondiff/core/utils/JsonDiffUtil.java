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
import neatlogic.framework.util.jsondiff.common.model.ComparatorEnum;
import neatlogic.framework.util.jsondiff.common.model.TravelPath;
import neatlogic.framework.util.jsondiff.common.model.neat.JsonNeat;
import neatlogic.framework.util.jsondiff.core.config.JsonDiffOption;
import neatlogic.framework.util.jsondiff.core.handle.array.AbstractArrayJsonNeat;
import neatlogic.framework.util.jsondiff.core.handle.object.AbstractObjectJsonNeat;
import neatlogic.framework.util.jsondiff.core.handle.primitive.AbstractPrimitiveJsonNeat;

public class JsonDiffUtil {


    /**
     * 判断两个对象应该采用什么比较器
     *
     * @param expect 期望对象
     * @param actual 实际对象
     * @return 比较器实例
     */
    public static JsonNeat getJsonNeat(Object expect, Object actual, TravelPath travelPath) {
        if (!ClassUtil.isSameClass(expect, actual)) {
            return null;
        }
        boolean defaultNeat = RunTimeDataFactory.getOptionInstance().isMandatoryDefaultNeat();
        Class<? extends JsonNeat> customComparator = JsonDiffOption.getJsonNeatFactory().getCustomComparator(travelPath.getAbstractTravelPath());
        boolean custom = customComparator != null;
        // 返回系统默认处理器
        if (expect instanceof JSONObject && actual instanceof JSONObject) {
            return custom ? selectionCustomJsonNeat(customComparator, ComparatorEnum.OBJECT) : JsonDiffOption.getJsonNeatFactory().getObjectJsonNeatInstance(defaultNeat);
        }
        if (expect instanceof JSONArray && actual instanceof JSONArray) {
            return custom ? selectionCustomJsonNeat(customComparator, ComparatorEnum.ARRAY) : JsonDiffOption.getJsonNeatFactory().getArrayJsonNeatInstance(defaultNeat);
        }
        if (ClassUtil.isPrimitiveType(expect) && ClassUtil.isPrimitiveType(actual)) {
            return custom ? selectionCustomJsonNeat(customComparator, ComparatorEnum.PRIMITIVE) : JsonDiffOption.getJsonNeatFactory().getPrimitiveJsonNeatInstance(defaultNeat);
        }
        return null;
    }

    /**
     * 判断传入的是否是一个合法处理器实现类
     *
     * @param customComparatorClass 自定义实现类
     * @param comparator            比较器类型
     * @return 用户比较器实例
     */
    private static JsonNeat selectionCustomJsonNeat(Class<? extends JsonNeat> customComparatorClass, ComparatorEnum comparator) {
        JsonNeat jsonNeat = null;
        try {
            jsonNeat = customComparatorClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new JsonDiffException(String.format("无法实例化自定义比较器: %s", customComparatorClass), e);
        }
        // 判断是否实现对应的类型
        switch (comparator) {
            case OBJECT:
                if (jsonNeat instanceof AbstractObjectJsonNeat) {
                    return jsonNeat;
                }
                throw new JsonDiffException(String.format("自定义比较器未继承%s", AbstractObjectJsonNeat.class.getName()));
            case ARRAY:
                if (jsonNeat instanceof AbstractArrayJsonNeat) {
                    return jsonNeat;
                }
                throw new JsonDiffException(String.format("自定义比较器未继承%s", AbstractArrayJsonNeat.class.getName()));
            case PRIMITIVE:
                if (jsonNeat instanceof AbstractPrimitiveJsonNeat) {
                    return jsonNeat;
                }
                throw new JsonDiffException(String.format("自定义比较器未继承%s", AbstractPrimitiveJsonNeat.class.getName()));
            default:
                throw new JsonDiffException("类型错误");
        }
    }


}
