/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.module.framework.restful.api;

import com.alibaba.fastjson.JSONObject;
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
