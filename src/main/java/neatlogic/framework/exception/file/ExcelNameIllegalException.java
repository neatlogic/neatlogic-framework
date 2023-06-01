package neatlogic.framework.exception.file;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ExcelNameIllegalException extends ApiRuntimeException {

    private static final long serialVersionUID = 1785871249959594461L;

    public ExcelNameIllegalException(String format) {
        super("Excel文件名称不合法，正确的格式为：{0}", format);
    }

}
