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

package neatlogic.module.framework.groupsearch.handler;

import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.common.constvalue.UserType;
import neatlogic.framework.restful.groupsearch.core.IGroupSearchHandler;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class CommonGroupHandler implements IGroupSearchHandler {
    @Override
    public String getName() {
        return GroupSearch.COMMON.getValue();
    }

    @Override
    public String getHeader() {
        return GroupSearch.COMMON.getValuePlugin();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> search(JSONObject jsonObj) {
        List<Object> includeList = jsonObj.getJSONArray("includeList");
        if (CollectionUtils.isEmpty(includeList)) {
            includeList = new ArrayList<>();
        }
        List<String> includeStrList = includeList.stream().map(Object::toString).collect(Collectors.toList());
        List<String> userTypeList = new ArrayList<>();
        for (UserType s : UserType.values()) {
            if ((s.getIsDefaultShow() || includeStrList.contains(getHeader() + s.getValue())) && s.getText().contains(jsonObj.getString("keyword"))) {
                userTypeList.add(s.getValue());
            }
        }
        return (List<T>) userTypeList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> reload(JSONObject jsonObj) {
        List<String> userTypeList = new ArrayList<String>();
        for(Object value :jsonObj.getJSONArray("valueList")) {
            if(value.toString().startsWith(getHeader())){
                userTypeList.add(value.toString().replace(getHeader(), ""));
            }
        }
        return (List<T>) userTypeList;
    }

    @Override
    public <T> JSONObject repack(List<T> userTypeList) {
        JSONObject userTypeObj = new JSONObject();
        userTypeObj.put("value", "common");
        userTypeObj.put("text", "公共");
        JSONArray userTypeArray = new JSONArray();
        for(T userType : userTypeList) {
            JSONObject userTypeTmp = new JSONObject();
            userTypeTmp.put("value", getHeader()+userType);
            userTypeTmp.put("text", UserType.getText(userType.toString()));
            userTypeArray.add(userTypeTmp);
        }
        userTypeObj.put("sort", getSort());
        userTypeObj.put("dataList", userTypeArray);
        return userTypeObj;
    }

    @Override
    public int getSort() {
        return 0;
    }

    @Override
    public Boolean isLimit() {
        return false;
    }
}
