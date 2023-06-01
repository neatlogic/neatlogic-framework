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

package neatlogic.framework.matrix.constvalue;

import neatlogic.framework.common.constvalue.IEnum;
import neatlogic.framework.matrix.core.IMatrixType;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;

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
        return I18nUtils.getMessage(name.toString());
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
