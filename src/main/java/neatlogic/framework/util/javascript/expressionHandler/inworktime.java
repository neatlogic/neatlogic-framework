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

package neatlogic.framework.util.javascript.expressionHandler;

import com.alibaba.fastjson.JSONArray;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.exception.util.javascript.ConditionIsNullException;
import neatlogic.framework.exception.util.javascript.ValueIsIrregularException;
import neatlogic.framework.exception.util.javascript.ValueIsNullException;
import neatlogic.framework.util.javascript.JavascriptUtil;
import neatlogic.framework.worktime.dao.mapper.WorktimeMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class inworktime {

    private static WorktimeMapper worktimeMapper;

    @Autowired
    public inworktime(WorktimeMapper _worktimeMapper) {
        this.worktimeMapper = _worktimeMapper;
    }

    public static boolean calculate(JSONArray dataValueList, JSONArray conditionValueList, String label) {
        String prefix = (StringUtils.isNotBlank(label) ? label + "的" : "");
        List<ApiRuntimeException> errorList = JavascriptUtil.getErrorList();
        String worktimeUuid;
        if (CollectionUtils.isNotEmpty(conditionValueList)) {
            worktimeUuid = conditionValueList.getString(0);
        } else {
            errorList.add(new ConditionIsNullException(prefix));
            return false;
        }

        if (CollectionUtils.isNotEmpty(dataValueList)) {
            try {
                long date = dataValueList.getLong(0);
                int count = worktimeMapper.checkIsWithinWorktimeRange(worktimeUuid, date);
                if (count > 0) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                errorList.add(new ValueIsIrregularException(prefix));
                return false;
            }
        } else {
            errorList.add(new ValueIsNullException(prefix));
            return false;
        }
    }
}
