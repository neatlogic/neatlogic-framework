package codedriver.framework.restful.groupsearch.handler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dto.UserVo;
import codedriver.framework.restful.groupsearch.core.IGroupSearchHandler;
@Service
public class UserGroupHandler implements IGroupSearchHandler {
	@Autowired
	private UserMapper userMapper;
	
	@Override
	public String getName() {
		return "user";
	}

	@Override
	public String getHeader() {
		return getName() + "#";
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
		userVo.setKeyword(jsonObj.getString("keyword"));
		userList = userMapper.searchUser(userVo);
		return (List<T>) userList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> reload(JSONObject jsonObj) {
		List<UserVo> userList = new ArrayList<UserVo>();
		List<String> userIdList = new ArrayList<String>();
		for (Object value : jsonObj.getJSONArray("valueList")) {
			if (value.toString().startsWith(getHeader())) {
				userIdList.add(value.toString().replace(getHeader(), ""));
			}
		}
		if (userIdList.size() > 0) {
			userList = userMapper.getUserByUserIdList(userIdList);
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
			userTmp.put("value", getHeader() + ((UserVo) user).getUserId());
			userTmp.put("text", ((UserVo) user).getUserName());
			userArray.add(userTmp);
		}
		userObj.put("sort", getSort());
		userObj.put("dataList", userArray);
		return userObj;
	}

	@Override
	public int getSort() {
		return 1;
	}

	@Override
	public Boolean isLimit() {
		return true;
	}
}
