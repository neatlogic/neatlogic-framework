package neatlogic.framework.exception.file;

import neatlogic.framework.exception.core.ApiRuntimeException;

@SuppressWarnings("serial")
public class FileTypeHandlerNotFoundException extends ApiRuntimeException {
    public FileTypeHandlerNotFoundException(String type) {
        super("exception.framework.filetypehandlernotfoundexception", type);
    }

}
