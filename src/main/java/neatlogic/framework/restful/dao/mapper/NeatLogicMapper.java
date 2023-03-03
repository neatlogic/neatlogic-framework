package neatlogic.framework.restful.dao.mapper;

import neatlogic.framework.dto.MongoDbVo;

public interface NeatLogicMapper {

	 MongoDbVo getMongodbByTenant(String tenantUuid);

	 MongoDbVo getMongodbList();

}
