package codedriver.framework.restful.dao.mapper;

import codedriver.framework.dto.MongoDbVo;

public interface CodedriverMapper {

	 MongoDbVo getMongodbByTenant(String tenantUuid);

	 MongoDbVo getMongodbList();

}
