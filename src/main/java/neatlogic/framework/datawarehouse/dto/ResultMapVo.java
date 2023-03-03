/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
