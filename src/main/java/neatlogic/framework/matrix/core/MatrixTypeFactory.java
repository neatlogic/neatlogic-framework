/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.framework.matrix.core;

import neatlogic.framework.common.constvalue.IEnum;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * @author linbq
 * @since 2021/11/16 15:24
 **/
public class MatrixTypeFactory implements IEnum {

    private static List<IMatrixType> list = new ArrayList<>();

    static {
        Reflections reflections = new Reflections("neatlogic");
        Set<Class<? extends IMatrixType>> classSet = reflections.getSubTypesOf(IMatrixType.class);
        for (Class<? extends IMatrixType> c : classSet) {
            for (IMatrixType obj : c.getEnumConstants()) {
                list.add(obj);
            }
        }
        list.sort(Comparator.comparingInt(IMatrixType::getSort));
    }

    public static String getName(String _value) {
        for (IMatrixType s : list) {
            if (s.getValue().equals(_value)) {
                return s.getName();
            }
        }
        return "";
    }

    @Override
    public List getValueTextList() {
        JSONArray array = new JSONArray();
        for(IMatrixType type : list){
            array.add(new JSONObject(){
                {
                    this.put("value",type.getValue());
                    this.put("text",type.getName());
                    this.put("key",type.getKey());
                }
            });
        }
        return array;
    }
}
