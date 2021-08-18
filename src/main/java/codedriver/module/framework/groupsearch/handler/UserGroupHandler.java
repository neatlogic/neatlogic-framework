/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.groupsearch.handler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.constvalue.GroupSearch;
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dto.UserVo;
import codedriver.framework.restful.groupsearch.core.IGroupSearchHandler;
@Service
public class UserGroupHandler implements IGroupSearchHandler {
	@Autowired
	private UserMapper userMapper;
	
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
