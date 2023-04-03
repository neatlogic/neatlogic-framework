package neatlogic.framework.exception.file;

import neatlogic.framework.exception.core.ApiRuntimeException;

@SuppressWarnings("serial")
public class FilePathIllegalException extends ApiRuntimeException {
    public FilePathIllegalException(String filePath) {
        super("exception.framework.filepathillegalexception", filePath);
    }

}
