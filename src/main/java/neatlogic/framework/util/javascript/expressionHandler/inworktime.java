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
