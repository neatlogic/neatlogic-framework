/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.matrix.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.IEnum;
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
