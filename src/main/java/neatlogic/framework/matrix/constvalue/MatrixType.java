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

package neatlogic.framework.matrix.constvalue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.IEnum;
import neatlogic.framework.matrix.core.IMatrixType;
import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;

import java.util.List;

public enum MatrixType implements IMatrixType, IEnum {
    CUSTOM("custom", new I18n("自定义数据源"), "custom", 1),
    EXTERNAL("external", new I18n("外部数据源"), "integrationUuid", 2),
    PRIVATE("private", new I18n("私有数据源"), "private", 5),
    VIEW("view", new I18n("数据库视图"), "fileId", 3);

    private String value;
    private I18n name;
    private String key;
    private int sort;

    MatrixType(String _value, I18n _name, String _key, int _sort) {
        this.value = _value;
        this.name = _name;
        this.key = _key;
        this.sort = _sort;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getName() {
        return $.t(name.toString());
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public int getSort() {
        return sort;
    }

    public static String getName(String _value) {
        for (MatrixType s : MatrixType.values()) {
            if (s.getValue().equals(_value)) {
                return s.getName();
            }
        }
        return "";
    }


    @Override
    public List getValueTextList() {
        JSONArray array = new JSONArray();
        for (MatrixType type : MatrixType.values()) {
            array.add(new JSONObject() {
                {
                    this.put("value", type.getValue());
                    this.put("text", type.getName());
                    this.put("key", type.getKey());
                }
            });
        }
        return array;
    }
}
