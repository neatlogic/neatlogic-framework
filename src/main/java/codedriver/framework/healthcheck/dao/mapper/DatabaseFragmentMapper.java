/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.healthcheck.dao.mapper;


import codedriver.framework.dto.healthcheck.DatabaseFragmentVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DatabaseFragmentMapper {
    List<DatabaseFragmentVo> searchDatabaseFragment(DatabaseFragmentVo databaseFragmentVo);

    int searchDatabaseFragmentCount(DatabaseFragmentVo databaseFragmentVo);

    void rebuildTable(@Param("schemaName") String schemaName, @Param("tableName") String tableName);
}

