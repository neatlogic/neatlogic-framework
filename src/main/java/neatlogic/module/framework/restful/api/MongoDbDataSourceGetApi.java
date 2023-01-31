/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.module.framework.restful.api;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.dto.MongoDbVo;
import neatlogic.framework.restful.annotation.*;
import neatlogic.framework.restful.constvalue.OperationTypeEnum;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentBase;
import neatlogic.framework.restful.dao.mapper.CodedriverMapper;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@OperationType(type = OperationTypeEnum.SEARCH)
public class MongoDbDataSourceGetApi extends PrivateApiComponentBase {
    @Autowired
    CodedriverMapper codedriverMapper;

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
        MongoDbVo mongodbVo = codedriverMapper.getMongodbByTenant(tenant);
        TenantContext.get().setUseDefaultDatasource(false);
        return mongodbVo;
    }
}
