package codedriver.framework.login.core;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dto.UserVo;

@Service
public class PwdLoginAuth extends LoginAuthBase {
	@Autowired
	UserMapper userMapper;
	
	@Override
	public String getType() {
		return "pwd";
	}

	@Override
	public UserVo myAuth(HttpServletRequest request,JSONObject jsonObj) {
		String userId = jsonObj.getString("userid");
		String password = jsonObj.getString("password");
		UserVo userVo = new UserVo();
		userVo.setUserId(userId);
		userVo.setPassword(password);
		return userMapper.getUserByUserIdAndPassword(userVo);
	}

}
