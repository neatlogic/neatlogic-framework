/*
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

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

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.dto.MongoDbVo;
import neatlogic.framework.restful.annotation.*;
import neatlogic.framework.restful.constvalue.OperationTypeEnum;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentBase;
import neatlogic.framework.restful.dao.mapper.NeatLogicMapper;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@OperationType(type = OperationTypeEnum.SEARCH)
public class MongoDbDataSourceGetApi extends PrivateApiComponentBase {
    @Autowired
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
        TenantContext.get().setUseDefaultDatasource(true);
        MongoDbVo mongodbVo = neatlogicMapper.getMongodbByTenant(tenant);
        TenantContext.get().setUseDefaultDatasource(false);
        return mongodbVo;
    }
}
