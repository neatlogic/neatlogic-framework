/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.datawarehouse.exceptions;

import codedriver.framework.exception.core.ApiRuntimeException;
import codedriver.framework.datawarehouse.dto.DataSourceVo;

public class ReportDataSourceSyncException extends ApiRuntimeException {
    public ReportDataSourceSyncException(DataSourceVo reportDataSourceVo, Exception ex) {
        super("数据源“" + reportDataSourceVo.getName() + "”数据同步失败，异常：" + ex.getMessage());
    }
}
