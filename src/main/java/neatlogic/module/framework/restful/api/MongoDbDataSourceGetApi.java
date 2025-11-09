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
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.auth.core.AuthAction;
import neatlogic.framework.auth.label.ADMIN;
import neatlogic.framework.common.constvalue.systemuser.SystemUser;
import neatlogic.framework.dto.MongoDbVo;
import neatlogic.framework.restful.annotation.*;
import neatlogic.framework.restful.constvalue.OperationTypeEnum;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentBase;
import neatlogic.framework.restful.dao.mapper.NeatLogicMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@AuthUser(SystemUser.AUTOEXEC)
@AuthAction(action = ADMIN.class)
@OperationType(type = OperationTypeEnum.SEARCH)
public class MongoDbDataSourceGetApi extends PrivateApiComponentBase {
    @Resource
    NeatLogicMapper neatlogicMapper;

    @Override
    public String getToken() {
        return "mongodb/datasource/get";
    }

    @Override
    public String getName() {
        return "获取mongodb连接信息";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Input({
    })
    @Output({
            @Param(name = "mongoVo", explode = MongoDbVo.class, desc = "mongodb数据库")
    })
    @Description(desc = "获取mongodb连接信息接口")
    @Override
    public Object myDoService(JSONObject jsonObj) throws Exception {
        String tenant = TenantContext.get().getTenantUuid();
        return neatlogicMapper.getMongodbByTenant(tenant);
    }
}
