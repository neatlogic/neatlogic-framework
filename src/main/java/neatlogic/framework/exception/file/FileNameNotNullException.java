package neatlogic.framework.exception.file;

import neatlogic.framework.exception.core.ApiRuntimeException;

import java.io.Serial;

public class FileNameNotNullException extends ApiRuntimeException {


    @Serial
    private static final long serialVersionUID = -8292132922966797066L;

    public FileNameNotNullException() {
        super("nfef.filenamenotnullexception.filenamenotnullexception");
    }

}
