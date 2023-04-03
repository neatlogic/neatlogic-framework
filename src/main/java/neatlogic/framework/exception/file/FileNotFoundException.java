package neatlogic.framework.exception.file;

import neatlogic.framework.exception.core.ApiRuntimeException;

@SuppressWarnings("serial")
public class FileNotFoundException extends ApiRuntimeException {
    public FileNotFoundException(Long id) {
        super("exception.framework.filenotfoundexception", id);
    }

}
