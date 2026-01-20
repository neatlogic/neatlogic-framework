package neatlogic.framework.userexportfile.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

import java.io.Serial;

public class UserExportFileDeleteDeniedException extends ApiRuntimeException {

    @Serial
    private static final long serialVersionUID = 3196317808541689156L;

    public UserExportFileDeleteDeniedException(String name) {
        super("nfue.userexportfiledeletedeniedexception.userexportfiledeletedeniedexception");
    }

}
