package neatlogic.framework.exception.file;

import neatlogic.framework.exception.core.ApiRuntimeException;

@SuppressWarnings("serial")
public class DirectoryCreateException extends ApiRuntimeException {
    public DirectoryCreateException(String filepath) {
        super("exception.framework.directorycreateexception", filepath);
    }

}
