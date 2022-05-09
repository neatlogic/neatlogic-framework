/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.datawarehouse.exceptions;

import codedriver.framework.exception.core.ApiRuntimeException;
import codedriver.framework.datawarehouse.dto.ReportDataSourceVo;

public class ReportDataSourceIsSyncingException extends ApiRuntimeException {
    public ReportDataSourceIsSyncingException(ReportDataSourceVo reportDataSourceVo) {
        super("数据源“" + reportDataSourceVo.getName() + "”同步数据中，请稍后再发起同步操作。");
    }
}
