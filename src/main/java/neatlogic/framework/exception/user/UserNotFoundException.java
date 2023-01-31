/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.exception.user;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class UserNotFoundException extends ApiRuntimeException {

    public UserNotFoundException(String userid) {
        super("用户“" + userid + "”不存在");
    }
}
