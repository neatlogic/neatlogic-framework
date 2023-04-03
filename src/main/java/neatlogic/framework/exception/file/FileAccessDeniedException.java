package neatlogic.framework.exception.file;

import neatlogic.framework.exception.core.ApiRuntimeException;

@SuppressWarnings("serial")
public class FileAccessDeniedException extends ApiRuntimeException {
    public FileAccessDeniedException(String filename, String operation) {
        super("exception.framework.fileaccessdeniedexception", operation, filename);
    }

}
