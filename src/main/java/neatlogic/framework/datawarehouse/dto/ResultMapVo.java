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

package neatlogic.framework.datawarehouse.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResultMapVo {
    private String id;
    private List<String> groupByList;
    private List<String> propertyList;
    private List<Map<String, Object>> resultList;
    //private List<ResultVo> resultList;
    private Map<String, ResultMapVo> resultMap;

    //param
    private String key;
    private int index;

    public List<Map<String, Object>> getResultList() {
        return resultList;
    }

    public void setResultList(List<Map<String, Object>> resultList) {
        this.resultList = resultList;
    }

    public void addGroupBy(String property) {
        if (this.groupByList == null) {
            this.groupByList = new ArrayList<String>();
        }
        this.groupByList.add(property);
    }

    public void addResult(Map<String, Object> result) {
        if (this.resultList == null) {
            this.resultList = new ArrayList<Map<String, Object>>();
        }
        this.resultList.add(result);
    }

    public void addProperty(String property) {
        if (this.propertyList == null) {
            this.propertyList = new ArrayList<String>();
        }
        this.propertyList.add(property);
    }

    public List<String> getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(List<String> propertyList) {
        this.propertyList = propertyList;
    }

	/*public void addResult(ResultVo resultVo){
		if(this.resultList == null){
			this.resultList = new ArrayList();
		}
		this.resultList.add(resultVo);
	}*/

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getGroupByList() {
        return groupByList;
    }

    public void setGroupByList(List<String> groupByList) {
        this.groupByList = groupByList;
    }
	/*public List<ResultVo> getResultList() {
		return resultList;
	}

	public void setResultList(List<ResultVo> resultList) {
		this.resultList = resultList;
	}*/

    public Map<String, ResultMapVo> getResultMap() {
        return resultMap;
    }

    public void setResultMap(Map<String, ResultMapVo> resultMap) {
        this.resultMap = resultMap;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
