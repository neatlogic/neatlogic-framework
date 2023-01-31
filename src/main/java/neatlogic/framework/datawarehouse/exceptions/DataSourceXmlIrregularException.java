/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.datawarehouse.exceptions;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class DataSourceXmlIrregularException extends ApiRuntimeException {
    //TODO 后续再按照异常类型分拆
    public DataSourceXmlIrregularException(String msg) {
        super(msg);
    }
}
