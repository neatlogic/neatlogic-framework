package neatlogic.framework.restful.dao.mapper;

import neatlogic.framework.dao.aop.UseMasterDatabase;
import neatlogic.framework.dto.MongoDbVo;

@UseMasterDatabase
public interface NeatLogicMapper {

    MongoDbVo getMongodbByTenant(String tenantUuid);

    MongoDbVo getMongodbList();

}
