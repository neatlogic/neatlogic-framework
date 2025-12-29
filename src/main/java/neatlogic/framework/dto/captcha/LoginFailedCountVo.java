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

package neatlogic.framework.dto.captcha;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

public class LoginFailedCountVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 8938457938146343100L;

    @EntityField(name = "用户id", type = ApiParamType.STRING)
    private String userId;
    @EntityField(name = "登录失败次数", type = ApiParamType.INTEGER)
    private Integer failedCount;
    @EntityField(name = "最近一次登录失败时间", type = ApiParamType.LONG)
    private Date lastFailedTime;
    @EntityField(name = "自动解锁时间", type = ApiParamType.LONG)
    private Date lockedUntil;

    public LoginFailedCountVo() {
    }

    public LoginFailedCountVo(String userId, int failedCount, Date lockedUntil, Date lastFailedTime) {
        this.failedCount = failedCount;
        this.userId = userId;
        this.lockedUntil = lockedUntil;
        this.lastFailedTime = lastFailedTime;

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

    public Date getLastFailedTime() {
        return lastFailedTime;
    }

    public void setLastFailedTime(Date lastFailedTime) {
        this.lastFailedTime = lastFailedTime;
    }

    public Date getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(Date lockedUntil) {
        this.lockedUntil = lockedUntil;
    }
}
