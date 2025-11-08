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

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

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

    protected T getConditionByUuid(String uuid) {
        for (T t : conditionList) {
            if (Objects.equals(t.getUuid(), uuid)) {
                return t;
            }
        }
        return null;
    }
}
