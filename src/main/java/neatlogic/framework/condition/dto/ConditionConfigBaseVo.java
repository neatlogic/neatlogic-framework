/*
 * Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.condition.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConditionConfigBaseVo<T extends ConditionGroupBaseVo<? extends ConditionBaseVo>> implements Serializable {

    private static final long serialVersionUID = 5439300427812355573L;

    protected List<T> conditionGroupList = new ArrayList<>();

    protected List<RelVo> conditionGroupRelList = new ArrayList<>();

    public List<T> getConditionGroupList() {
        return conditionGroupList;
    }

    public void setConditionGroupList(List<T> conditionGroupList) {
        this.conditionGroupList = conditionGroupList;
    }

    public List<RelVo> getConditionGroupRelList() {
        return conditionGroupRelList;
    }

    public void setConditionGroupRelList(List<RelVo> conditionGroupRelList) {
        this.conditionGroupRelList = conditionGroupRelList;
    }

    protected T getConditionGroupByUuid(String uuid) {
        for (T t : conditionGroupList) {
            if (Objects.equals(t.getUuid(), uuid)) {
                return t;
            }
        }
        return null;
    }
}
