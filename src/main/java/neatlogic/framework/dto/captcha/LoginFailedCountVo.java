/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.dto.captcha;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;

import java.io.Serializable;

public class LoginFailedCountVo implements Serializable {
    private static final long serialVersionUID = -1911666621870552499L;
    @EntityField(name = "用户uuid", type = ApiParamType.STRING)
    private String userId;
    @EntityField(name = "登录失败次数", type = ApiParamType.INTEGER)
    private Integer failedCount;

    public LoginFailedCountVo() {
    }

    public LoginFailedCountVo(String userId, int count) {
        this.failedCount = count;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(Integer failedCount) {
        this.failedCount = failedCount;
    }
}
