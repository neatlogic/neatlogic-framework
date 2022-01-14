/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.restful.api;

import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.dao.mapper.UserSessionMapper;
import codedriver.framework.restful.annotation.Description;
import codedriver.framework.restful.annotation.Input;
import codedriver.framework.restful.annotation.OperationType;
import codedriver.framework.restful.annotation.Output;
import codedriver.framework.restful.constvalue.OperationTypeEnum;
import codedriver.framework.restful.core.privateapi.PrivateApiComponentBase;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@OperationType(type = OperationTypeEnum.OPERATE)
public class LogoutApi extends PrivateApiComponentBase {
	@Resource
	UserSessionMapper userSessionMapper;

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
		userSessionMapper.deleteUserSessionByUserUuid(UserContext.get().getUserUuid(true));
		return null; 
	}
}
