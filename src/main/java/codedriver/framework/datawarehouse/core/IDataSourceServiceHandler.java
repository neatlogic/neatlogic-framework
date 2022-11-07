/*
 * Copyright (c)  2022 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.datawarehouse.core;

import codedriver.framework.datawarehouse.dto.DataSourceAuditVo;
import codedriver.framework.datawarehouse.dto.DataSourceVo;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface IDataSourceServiceHandler {
    /**
     * 处理器
     * @return 处理器
     */
    String getHandler();

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void syncData(DataSourceVo dataSourceVo, DataSourceAuditVo reportDataSourceAuditVo);
}
