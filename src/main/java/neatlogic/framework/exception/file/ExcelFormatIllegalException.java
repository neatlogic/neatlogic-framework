/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.exception.file;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ExcelFormatIllegalException extends ApiRuntimeException {


    public ExcelFormatIllegalException() {
        super("Excel文件格式错误，请上传xls或xlsx格式的Excel文件");
    }

    public ExcelFormatIllegalException(String format) {
        super("Excel文件格式错误，请上传" + format + "格式的Excel文件");
    }

}
