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

package neatlogic.framework.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.restful.annotation.EntityField;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

public class UserPasswordVo extends BasePageVo implements Serializable {
    @Serial
    private static final long serialVersionUID = -7695453599016163399L;
    @EntityField(name = "id", type = ApiParamType.LONG)
    private Long id;
    @EntityField(name = "用户uuid", type = ApiParamType.STRING)
    private String userUuid;
    @EntityField(name = "用户id", type = ApiParamType.STRING)
    private String userId;
    @EntityField(name = "密码创建时间", type = ApiParamType.LONG)
    private Date createTime;
    @EntityField(name = "是否激活", type = ApiParamType.INTEGER)
    private Date isActive;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getIsActive() {
        return isActive;
    }

    public void setIsActive(Date isActive) {
        this.isActive = isActive;
    }
}
