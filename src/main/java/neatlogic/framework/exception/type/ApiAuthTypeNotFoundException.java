/*
 * Copyright(c) 2021. TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.exception.type;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ApiAuthTypeNotFoundException extends ApiRuntimeException {

	private static final long serialVersionUID = -5142624297760626820L;

	public ApiAuthTypeNotFoundException(String msg) {
		super("不存在的认证方式：" + msg);
	}

}
