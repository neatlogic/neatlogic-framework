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

package neatlogic.framework.condition.dto;

import neatlogic.framework.common.dto.BaseEditorVo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConditionConfigBaseVo<T extends ConditionGroupBaseVo<? extends ConditionBaseVo>> extends BaseEditorVo implements Serializable {

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
