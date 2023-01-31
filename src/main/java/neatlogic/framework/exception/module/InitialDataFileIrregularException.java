/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.exception.module;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class InitialDataFileIrregularException extends ApiRuntimeException {
    public InitialDataFileIrregularException() {
        super("导入文件没通过校验，请选择正确的导入文件");
    }
}
