/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
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
