package neatlogic.framework.exception.file;

import neatlogic.framework.exception.core.ApiRuntimeException;

@SuppressWarnings("serial")
public class FileTypeConfigNotFoundException extends ApiRuntimeException {
    public FileTypeConfigNotFoundException(String belong) {
        super("exception.framework.filetypeconfignotfoundexception", belong);
    }

}
