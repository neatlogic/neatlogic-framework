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
