package neatlogic.framework.restful.dao.mapper;

import neatlogic.framework.dto.MongoDbVo;

public interface CodedriverMapper {

	 MongoDbVo getMongodbByTenant(String tenantUuid);

	 MongoDbVo getMongodbList();

}
