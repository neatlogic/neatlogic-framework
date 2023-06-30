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

import neatlogic.framework.util.jsondiff.common.model.neat.JsonNeat;
import neatlogic.framework.util.jsondiff.core.handle.array.ComplexArrayJsonNeat;
import neatlogic.framework.util.jsondiff.core.handle.object.ComplexObjectJsonNeat;
import neatlogic.framework.util.jsondiff.core.handle.primitive.PrimitiveTypeJsonNeat;

import java.util.HashMap;
import java.util.Map;

public class JsonNeatFactory {

    /**
     * 对象默认比较器
     */
    private Class<? extends JsonNeat> objectJsonNeat = ComplexObjectJsonNeat.class;

    /**
     * 数组默认比较器
     */
    private Class<? extends JsonNeat> arrayJsonNeat = ComplexArrayJsonNeat.class;

    /**
     * 基本类型默认比较器
     */
    private Class<? extends JsonNeat> primitiveJsonNeat = PrimitiveTypeJsonNeat.class;

    /**
     * 指定的path使用自定义比较器
     * key: 与ignorePath格式一致
     * value: 继承 AbstractArrayJsonNeat,AbstractObjectJsonNeat,AbstractPrimitiveJsonNeat. 并且实现对应格式接口的字节码
     */
    private final Map<String, Class<? extends JsonNeat>> customComparator = new HashMap<>();


    public Class<? extends JsonNeat> getObjectJsonNeat(boolean defaultNeat) {
        if (defaultNeat) {
            return ComplexObjectJsonNeat.class;
        }
        return objectJsonNeat;
    }

    public JsonNeat getObjectJsonNeatInstance(boolean defaultNeat) {
        return ClassUtil.getClassNameInstance(getObjectJsonNeat(defaultNeat));
    }

    public void setObjectJsonNeat(Class<? extends JsonNeat> objectJsonNeat) {
        this.objectJsonNeat = objectJsonNeat;
    }

    public Class<? extends JsonNeat> getArrayJsonNeat(boolean defaultNeat) {
        if (defaultNeat) {
            return ComplexArrayJsonNeat.class;
        }
        return arrayJsonNeat;
    }

    public JsonNeat getArrayJsonNeatInstance(boolean defaultNeat) {
        return ClassUtil.getClassNameInstance(getArrayJsonNeat(defaultNeat));
    }

    public void setArrayJsonNeat(Class<? extends JsonNeat> arrayJsonNeat) {
        this.arrayJsonNeat = arrayJsonNeat;
    }

    public Class<? extends JsonNeat> getPrimitiveJsonNeat(boolean defaultNeat) {
        if (defaultNeat) {
            return PrimitiveTypeJsonNeat.class;
        }
        return primitiveJsonNeat;
    }

    public JsonNeat getPrimitiveJsonNeatInstance(boolean defaultNeat) {
        return ClassUtil.getClassNameInstance(getPrimitiveJsonNeat(defaultNeat));
    }

    public void setPrimitiveJsonNeat(Class<? extends JsonNeat> primitiveJsonNeat) {
        this.primitiveJsonNeat = primitiveJsonNeat;
    }

    public Class<? extends JsonNeat> getCustomComparator(String path) {
        return customComparator.get(path);
    }

    public void addCustomComparator(String path, Class<? extends JsonNeat> customComparator) {
        this.customComparator.put(path, customComparator);
    }

}
