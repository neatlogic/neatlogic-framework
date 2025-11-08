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

package neatlogic.framework.util.jsondiff.common.model;


import java.util.ArrayList;
import java.util.List;

public class JsonCompareResult {

    /**
     * 是否符合对比要求
     */
    private Boolean match = true;

    /**
     * 差异列表
     */
    private List<Defects> defectsList;


    public boolean isMatch() {
        if (match == null) {
            return false;
        }
        return match;
    }

    /**
     * 添加对比信息
     *
     * @param defects 不一致的信息
     * @return 返回是否添加成功
     */
    public boolean addDefects(Defects defects) {
        if (defectsList == null) {
            defectsList = new ArrayList<>();
        }
        if (match) {
            match = false;
        }
        defectsList.add(defects);
        return true;
    }

    public void mergeDefects(List<Defects> defectsList) {
        if (defectsList == null || defectsList.size() == 0) {
            return;
        }
        match = false;
        if (this.defectsList == null) {
            this.defectsList = new ArrayList<>();
        }
        this.defectsList.addAll(defectsList);
    }

    public void setMatch(Boolean match) {
        this.match = match;
    }

    public List<Defects> getDefectsList() {
        return defectsList;
    }

    public void setDefectsList(List<Defects> defectsList) {
        this.defectsList = defectsList;
    }
}
