package codedriver.framework.restful.api;

import codedriver.framework.auth.core.AuthAction;
import codedriver.framework.auth.label.NO_AUTH;
import codedriver.framework.restful.core.constvalue.OperationTypeEnum;
import codedriver.framework.restful.annotation.OperationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.restful.annotation.Description;
import codedriver.framework.restful.annotation.Input;
import codedriver.framework.restful.annotation.Output;
import codedriver.framework.restful.core.privateapi.PrivateApiComponentBase;

@Service
@AuthAction(action = NO_AUTH.class)
@OperationType(type = OperationTypeEnum.OPERATE)
public class LogoutApi extends PrivateApiComponentBase {
	@Autowired
	UserMapper userMapper;

	@Override
	public String getToken() {
		return "logout";
	}

	@Override
	public String getName() {
		return "登出接口";
	}

	@Override
	public String getConfig() {
		return null;
	}

	@Input({})
	@Output({})
	@Description(desc = "登出接口")
	@Override
	public Object myDoService(JSONObject jsonObj) throws Exception {
		userMapper.deleteUserSessionByUserUuid(UserContext.get().getUserUuid(true));
		return null; 
	}
}
