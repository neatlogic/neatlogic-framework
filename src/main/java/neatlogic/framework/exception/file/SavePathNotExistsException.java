package neatlogic.framework.exception.file;

import neatlogic.framework.exception.core.ApiRuntimeException;

@SuppressWarnings("serial")
public class SavePathNotExistsException extends ApiRuntimeException {
    public SavePathNotExistsException(String belong) {
        super("exception.framework.savepathnotexistsexception", belong);
    }

}
