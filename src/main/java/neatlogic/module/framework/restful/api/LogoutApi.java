/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.module.framework.restful.api;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
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
		if(StringUtils.isBlank(Config.LOGIN_AUTH_HANDLER())){
			loginAuth = LoginAuthFactory.getLoginAuth("default");
		}else{
			loginAuth = LoginAuthFactory.getLoginAuth(Config.LOGIN_AUTH_HANDLER());
		}
		if(loginAuth == null){
			throw new LoginAuthNotFoundException(Config.LOGIN_AUTH_HANDLER());
		}
		String url = loginAuth.logout();
		JSONObject returnObj = new JSONObject();
		returnObj.put("url", Strings.isNullOrEmpty(url) ? "" : url);
		return returnObj;
	}
}
