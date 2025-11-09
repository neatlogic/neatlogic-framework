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

package neatlogic.framework.extramenu.constvalue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.IEnum;

import java.util.List;

public enum ExtraMenuType implements IEnum {
    DIRECTORY(0), MENU(1);

    private int type;

    ExtraMenuType(int _type) {
        this.type = _type;
    }

    @Override
    public List getValueTextList() {
        JSONArray array = new JSONArray();
        for (ExtraMenuType typeEnum : ExtraMenuType.values()) {
            array.add(new JSONObject() {
                {
                    this.put("value", typeEnum.getType());
                    this.put("text", typeEnum.name());
                }
            });
        }
        return array;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
