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
import neatlogic.framework.common.constvalue.DeviceType;
import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.common.constvalue.UserType;
import neatlogic.framework.common.util.CommonUtil;
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.restful.groupsearch.core.GroupSearchGroupVo;
import neatlogic.framework.restful.groupsearch.core.GroupSearchOptionVo;
import neatlogic.framework.restful.groupsearch.core.GroupSearchVo;
import neatlogic.framework.restful.groupsearch.core.IGroupSearchHandler;
import neatlogic.framework.service.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserGroupHandler implements IGroupSearchHandler {
    @Resource
    private UserMapper userMapper;
    @Resource
    UserService userService;

    @Override
    public String getName() {
        return GroupSearch.USER.getValue();
    }

    @Override
    public String getLabel() {
        return GroupSearch.USER.getText();
    }

    @Override
    public String getHeader() {
        return GroupSearch.USER.getValuePlugin();
    }

    @Override
    public List<GroupSearchOptionVo> search(GroupSearchVo groupSearchVo) {
        //总显示选项个数
//        Integer total = jsonObj.getInteger("total");
        Integer total = groupSearchVo.getTotal();
        if (total == null) {
            total = 18;
        }
        List<UserVo> userList = new ArrayList<UserVo>();
        UserVo userVo = new UserVo();
        userVo.setNeedPage(true);
        userVo.setPageSize(total);
        userVo.setCurrentPage(1);
        userVo.setIsActive(1);
        userVo.setIsDelete(0);
//        userVo.setKeyword(jsonObj.getString("keyword"));
        userVo.setKeyword(groupSearchVo.getKeyword());
        //如果存在rangeList 则需要过滤option
//        JSONArray rangeList = jsonObj.getJSONArray("rangeList");
//        if (CollectionUtils.isNotEmpty(rangeList)) {
//            userService.getUserByRangeList(userVo, rangeList.stream().map(Object::toString).collect(Collectors.toList()));
//        }
        List<String> rangeList = groupSearchVo.getRangeList();
        if (CollectionUtils.isNotEmpty(rangeList)) {
            userService.getUserByRangeList(userVo, rangeList);
        }
        userList = userMapper.searchUser(userVo);
        return convertGroupSearchOption(userList);
    }

    @Override
    public List<GroupSearchOptionVo> reload(GroupSearchVo groupSearchVo) {
        List<UserVo> userList = new ArrayList<>();
        List<String> userUuidList = new ArrayList<>();
//        List<String> valueList = JSONObject.parseArray(jsonObj.getJSONArray("valueList").toJSONString(), String.class);
//        for (Object value : valueList) {
//            if (value.toString().startsWith(getHeader())) {
//                userUuidList.add(value.toString().replace(getHeader(), ""));
//            }
//        }
        for (String value : groupSearchVo.getValueList()) {
            if (value.startsWith(getHeader())) {
                userUuidList.add(value.replace(getHeader(), StringUtils.EMPTY));
            }
        }
        if (!userUuidList.isEmpty()) {
            userList = userMapper.getUserByUserUuidList(userUuidList);
        }
        return convertGroupSearchOption(userList);
    }

    private List<GroupSearchOptionVo> convertGroupSearchOption(List<UserVo> userList) {
        List<GroupSearchOptionVo> dataList = new ArrayList<>();
        for (UserVo userVo : userList) {
            GroupSearchOptionVo groupSearchOptionVo = new GroupSearchOptionVo();
            groupSearchOptionVo.setValue(getHeader() + userVo.getUuid());
            groupSearchOptionVo.setText(userVo.getUserName() + "(" + userVo.getUserId() + ")");
            //移动端临时屏蔽这两个字段，表单也会用到这个接口
            if (!Objects.equals(DeviceType.MOBILE.getValue(), CommonUtil.getDevice())) {
                groupSearchOptionVo.setPinyin(userVo.getPinyin());
                groupSearchOptionVo.setTeam(String.join(",", userVo.getTeamNameList()));// TODO 分隔符改成前端设置
            }
            groupSearchOptionVo.setAvatar(userVo.getAvatar());
            groupSearchOptionVo.setVipLevel(userVo.getVipLevel());
            dataList.add(groupSearchOptionVo);
        }
        return dataList;
    }

//    @Override
    public GroupSearchGroupVo repack(List<UserVo> userList) {
        GroupSearchGroupVo groupSearchGroupVo = new GroupSearchGroupVo();
        groupSearchGroupVo.setValue("user");
        groupSearchGroupVo.setText("用户");
        groupSearchGroupVo.setSort(getSort());
        List<GroupSearchOptionVo> dataList = new ArrayList<>();
        for (UserVo userVo : userList) {
            GroupSearchOptionVo groupSearchOptionVo = new GroupSearchOptionVo();
            groupSearchOptionVo.setValue(getHeader() + userVo.getUuid());
            groupSearchOptionVo.setText(userVo.getUserName() + "(" + userVo.getUserId() + ")");
            //移动端临时屏蔽这两个字段，表单也会用到这个接口
            if (!Objects.equals(DeviceType.MOBILE.getValue(), CommonUtil.getDevice())) {
                groupSearchOptionVo.setPinyin(userVo.getPinyin());
                groupSearchOptionVo.setTeam(String.join(",", userVo.getTeamNameList()));// TODO 分隔符改成前端设置
            }
            groupSearchOptionVo.setAvatar(userVo.getAvatar());
            groupSearchOptionVo.setVipLevel(userVo.getVipLevel());
            dataList.add(groupSearchOptionVo);
        }
        groupSearchGroupVo.setDataList(dataList);
        return groupSearchGroupVo;
//        JSONObject userObj = new JSONObject();
//        userObj.put("value", "user");
//        userObj.put("text", "用户");
//        JSONArray userArray = new JSONArray();
//
//        for (UserVo userVo : userList) {
//            JSONObject userTmp = new JSONObject();
//            userTmp.put("value", getHeader() + userVo.getUuid());
//            userTmp.put("text", userVo.getUserName() + "(" + userVo.getUserId() + ")");
////			userTmp.put("userInfo", ((UserVo) user).getUserInfo());
//            //移动端临时屏蔽这两个字段，表单也会用到这个接口
//            if (!Objects.equals(DeviceType.MOBILE.getValue(), CommonUtil.getDevice())) {
//                userTmp.put("pinyin", userVo.getPinyin());
//                userTmp.put("team", String.join(",", userVo.getTeamNameList()));// TODO 分隔符改成前端设置
//            }
//            userTmp.put("avatar", userVo.getAvatar());
//            userTmp.put("vipLevel", userVo.getVipLevel());
//            userArray.add(userTmp);
//        }
//        userObj.put("sort", getSort());
//        userObj.put("dataList", userArray);
//        return userObj;
    }

    @Override
    public int getSort() {
        return 2;
    }

    @Override
    public Boolean isLimit() {
        return true;
    }
}
