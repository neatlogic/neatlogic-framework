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
