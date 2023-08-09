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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.common.constvalue.UserType;
import neatlogic.framework.restful.groupsearch.core.GroupSearchGroupVo;
import neatlogic.framework.restful.groupsearch.core.GroupSearchOptionVo;
import neatlogic.framework.restful.groupsearch.core.GroupSearchVo;
import neatlogic.framework.restful.groupsearch.core.IGroupSearchHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommonGroupHandler implements IGroupSearchHandler {
    @Override
    public String getName() {
        return GroupSearch.COMMON.getValue();
    }

    @Override
    public String getLabel() {
        return GroupSearch.COMMON.getText();
    }

    @Override
    public String getHeader() {
        return GroupSearch.COMMON.getValuePlugin();
    }

    @Override
    public List<GroupSearchOptionVo> search(GroupSearchVo groupSearchVo) {
//        List<Object> includeList = jsonObj.getJSONArray("includeList");
//        if (CollectionUtils.isEmpty(includeList)) {
//            includeList = new ArrayList<>();
//        }
//        List<String> includeStrList = includeList.stream().map(Object::toString).collect(Collectors.toList());
        List<String> includeStrList = groupSearchVo.getIncludeList();
        if (CollectionUtils.isEmpty(includeStrList)) {
            includeStrList = new ArrayList<>();
        }
        List<GroupSearchOptionVo> groupSearchOptionList = new ArrayList<>();
        for (UserType s : UserType.values()) {
            if ((s.getIsDefaultShow() || includeStrList.contains(getHeader() + s.getValue())) && s.getText().contains(groupSearchVo.getKeyword())) {
                GroupSearchOptionVo groupSearchOptionVo = new GroupSearchOptionVo();
                groupSearchOptionVo.setValue(s.getValue());
                groupSearchOptionVo.setText(s.getText());
                groupSearchOptionList.add(groupSearchOptionVo);
            }
        }
        return groupSearchOptionList;
    }

    @Override
    public List<GroupSearchOptionVo> reload(GroupSearchVo groupSearchVo) {
        List<GroupSearchOptionVo> groupSearchOptionList = new ArrayList<>();
        for (String value : groupSearchVo.getValueList()) {
            if (value.startsWith(getHeader())) {
                value = value.replace(getHeader(), StringUtils.EMPTY);
                GroupSearchOptionVo groupSearchOptionVo = new GroupSearchOptionVo();
                groupSearchOptionVo.setValue(value);
                groupSearchOptionVo.setText(UserType.getText(value));
            }
        }
//        for (Object value : jsonObj.getJSONArray("valueList")) {
//            if (value.toString().startsWith(getHeader())) {
//                userTypeList.add(value.toString().replace(getHeader(), ""));
//            }
//        }
        return groupSearchOptionList;
    }

//    @Override
    public GroupSearchGroupVo repack(List<String> userTypeList) {
        GroupSearchGroupVo groupSearchGroupVo = new GroupSearchGroupVo();
        groupSearchGroupVo.setValue("common");
        groupSearchGroupVo.setText("公共");
        groupSearchGroupVo.setSort(getSort());
        List<GroupSearchOptionVo> dataList = new ArrayList<>();
        for (String userType : userTypeList) {
            GroupSearchOptionVo groupSearchOptionVo = new GroupSearchOptionVo();
            groupSearchOptionVo.setValue(getHeader() + userType);
            groupSearchOptionVo.setText(UserType.getText(userType));
            dataList.add(groupSearchOptionVo);
        }
        groupSearchGroupVo.setDataList(dataList);
        return groupSearchGroupVo;
//        JSONObject userTypeObj = new JSONObject();
//        userTypeObj.put("value", "common");
//        userTypeObj.put("text", "公共");
//        JSONArray userTypeArray = new JSONArray();
//        for (String userType : userTypeList) {
//            JSONObject userTypeTmp = new JSONObject();
//            userTypeTmp.put("value", getHeader() + userType);
//            userTypeTmp.put("text", UserType.getText(userType));
//            userTypeArray.add(userTypeTmp);
//        }
//        userTypeObj.put("sort", getSort());
//        userTypeObj.put("dataList", userTypeArray);
//        return userTypeObj;
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
