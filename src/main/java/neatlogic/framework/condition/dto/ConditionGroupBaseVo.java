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

package neatlogic.framework.condition.dto;

import java.io.Serializable;
import java.util.List;

public class ConditionGroupBaseVo<T extends ConditionBaseVo> implements Serializable {
    private static final long serialVersionUID = 8392325201425982471L;

    protected String uuid;
    protected List<T> conditionList;
    protected List<RelVo> conditionRelList;

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public List<T> getConditionList() {
        return conditionList;
    }

    public void setConditionList(List<T> conditionList) {
        this.conditionList = conditionList;
    }

    public List<RelVo> getConditionRelList() {
        return conditionRelList;
    }

    public void setConditionRelList(List<RelVo> conditionRelList) {
        this.conditionRelList = conditionRelList;
    }

}
