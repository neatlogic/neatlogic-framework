package codedriver.framework.login.core;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.dto.UserVo;

public interface ILoginAuth {
	
	String getType();
	
	UserVo auth(HttpServletRequest request,JSONObject jsonObj);
}
