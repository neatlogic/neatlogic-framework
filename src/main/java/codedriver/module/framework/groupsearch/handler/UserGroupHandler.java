/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.groupsearch.handler;

import codedriver.framework.common.constvalue.GroupSearch;
import codedriver.framework.dao.mapper.RoleMapper;
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dto.RoleTeamVo;
import codedriver.framework.dto.RoleUserVo;
import codedriver.framework.dto.UserVo;
import codedriver.framework.restful.groupsearch.core.IGroupSearchHandler;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserGroupHandler implements IGroupSearchHandler {
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private RoleMapper roleMapper;
	
	@Override
	public String getName() {
		return GroupSearch.USER.getValue();
	}

	@Override
	public String getHeader() {
		return GroupSearch.USER.getValuePlugin();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> search(JSONObject jsonObj) {
		//总显示选项个数
		Integer total = jsonObj.getInteger("total");
		if(total == null) {
			total = 18;
		}
		List<UserVo> userList = new ArrayList<UserVo>();
		UserVo userVo = new UserVo();
		userVo.setNeedPage(true);
		userVo.setPageSize(total);
		userVo.setCurrentPage(1);
		userVo.setIsActive(1);
		userVo.setKeyword(jsonObj.getString("keyword"));
		//如果存在rangeList 则需要过滤option
		List<Object> rangeList = jsonObj.getJSONArray("rangeList");
		if(CollectionUtils.isNotEmpty(rangeList)){
			List<String> roleList = new ArrayList<>();
			Set<String> teamSet = new HashSet<>();
			Set<String> parentTeamSet = new HashSet<>();
			Set<String> userSet = new HashSet<>();
			rangeList.forEach(r->{
				if(r.toString().startsWith(GroupSearch.ROLE.getValuePlugin())){
					roleList.add(GroupSearch.removePrefix(r.toString()));
				} else if(r.toString().startsWith(GroupSearch.TEAM.getValuePlugin())){
					teamSet.add(GroupSearch.removePrefix(r.toString()));
				}else if(r.toString().startsWith(GroupSearch.USER.getValuePlugin())){
					userSet.add(GroupSearch.removePrefix(r.toString()));
				}
			});
			if(CollectionUtils.isNotEmpty(roleList)) {
				List<RoleTeamVo> roleTeamVoList = roleMapper.getRoleTeamListByRoleUuidList(roleList);
				roleTeamVoList.forEach(rt->{
					if(rt.getCheckedChildren() == 1){//如果组穿透
						parentTeamSet.add(rt.getTeamUuid());
					}else{
						teamSet.add(rt.getTeamUuid());
					}
				});
				List<RoleUserVo> roleUserVoList = roleMapper.getRoleUserListByRoleUuidList(roleList);
				roleUserVoList.forEach(ru->{
					userSet.add(ru.getUserUuid());
				});
			}
			userVo.setRangeList(rangeList.stream().map(Object::toString).collect(Collectors.toList()));
			userVo.setUserUuidList(new ArrayList<>(userSet));
			userVo.setTeamUuidList(new ArrayList<>(teamSet));
			userVo.setParentTeamUuidList(new ArrayList<>(parentTeamSet));
		}
		userList = userMapper.searchUser(userVo);
		return (List<T>) userList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> reload(JSONObject jsonObj) {
		List<UserVo> userList = new ArrayList<UserVo>();
		List<String> userUuidList = new ArrayList<String>();
		List<String> valueList = JSONObject.parseArray(jsonObj.getJSONArray("valueList").toJSONString(), String.class);
		for (Object value : valueList) {
			if (value.toString().startsWith(getHeader())) {
				userUuidList.add(value.toString().replace(getHeader(), ""));
			}
		}
		if (userUuidList.size() > 0) {
			userList = userMapper.getUserByUserUuidList(userUuidList);
		}
		return (List<T>) userList;
	}

	@Override
	public <T> JSONObject repack(List<T> userList) {
		JSONObject userObj = new JSONObject();
		userObj.put("value", "user");
		userObj.put("text", "用户");
		JSONArray userArray = new JSONArray();
		
		for (T user : userList) {
			JSONObject userTmp = new JSONObject();
			userTmp.put("value", getHeader() + ((UserVo) user).getUuid());
			userTmp.put("text", ((UserVo) user).getUserName() + "(" + ((UserVo) user).getUserId() + ")");
//			userTmp.put("userInfo", ((UserVo) user).getUserInfo());
			userTmp.put("pinyin",((UserVo) user).getPinyin());
			userTmp.put("avatar", ((UserVo) user).getAvatar());
			userTmp.put("vipLevel", ((UserVo) user).getVipLevel());
			userArray.add(userTmp);
		}
		userObj.put("sort", getSort());
		userObj.put("dataList", userArray);
		return userObj;
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
