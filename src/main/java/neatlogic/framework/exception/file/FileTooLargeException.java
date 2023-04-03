package neatlogic.framework.exception.file;

import neatlogic.framework.exception.core.ApiRuntimeException;

@SuppressWarnings("serial")
public class FileTooLargeException extends ApiRuntimeException {
    public FileTooLargeException(Long fileSize, Long maxSize) {
        super("exception.framework.filetoolargeexception", maxSize, fileSize);
    }

}
