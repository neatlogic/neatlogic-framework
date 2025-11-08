/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

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
