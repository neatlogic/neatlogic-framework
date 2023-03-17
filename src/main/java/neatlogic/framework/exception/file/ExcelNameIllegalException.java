package neatlogic.framework.exception.file;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ExcelNameIllegalException extends ApiRuntimeException {

    private static final long serialVersionUID = 1785871249959594461L;

    public ExcelNameIllegalException(String format) {
        super("exception.framework.excelnameillegalexception", format);
    }

}
