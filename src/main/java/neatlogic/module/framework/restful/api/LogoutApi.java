/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.module.framework.restful.api;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.auth.core.AuthAction;
import neatlogic.framework.auth.label.NoAuth;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.exception.login.LoginAuthNotFoundException;
import neatlogic.framework.filter.core.ILoginAuthHandler;
import neatlogic.framework.filter.core.LoginAuthFactory;
import neatlogic.framework.restful.annotation.Description;
import neatlogic.framework.restful.annotation.Input;
import neatlogic.framework.restful.annotation.OperationType;
import neatlogic.framework.restful.annotation.Output;
import neatlogic.framework.restful.constvalue.OperationTypeEnum;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentBase;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@AuthAction(action = NoAuth.class)
@OperationType(type = OperationTypeEnum.OPERATE)
public class LogoutApi extends PrivateApiComponentBase {

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
		ILoginAuthHandler loginAuth;
		if(StringUtils.isBlank(Config.LOGIN_AUTH_TYPE())){
			loginAuth = LoginAuthFactory.getLoginAuth("default");
		}else{
			loginAuth = LoginAuthFactory.getLoginAuth(Config.LOGIN_AUTH_TYPE());
		}
		if(loginAuth == null){
			throw new LoginAuthNotFoundException(Config.LOGIN_AUTH_TYPE());
		}
		String url = loginAuth.logout();
		JSONObject returnObj = new JSONObject();
		returnObj.put("url", StringUtils.isBlank(url) ? StringUtils.EMPTY : url);
		return returnObj;
	}
}
