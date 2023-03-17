package neatlogic.framework.exception.file;

import neatlogic.framework.exception.core.ApiRuntimeException;

@SuppressWarnings("serial")
public class FileExtNotAllowedException extends ApiRuntimeException {
    public FileExtNotAllowedException(String fileExt) {
        super("exception.framework.fileextnotallowedexception", fileExt);
    }

}
