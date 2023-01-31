/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.dao.mapper;

import neatlogic.framework.dto.MongoDbVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MongoDbMapper {
    List<MongoDbVo> getAllActiveTenantMongoDb();

    List<MongoDbVo> getAllTenantMongoDb();

    int updateTenantMongoDbPasswordByTenantId(@Param("tenantId") Long tenantId, @Param("password") String password);
}
