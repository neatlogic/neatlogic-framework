package neatlogic.framework.exception.user;

import neatlogic.framework.exception.core.ApiRuntimeException;

@Deprecated
public class UserAgentRepeatException extends ApiRuntimeException {

    private static final long serialVersionUID = 5732908853777656246L;

    @Deprecated
    public UserAgentRepeatException(String userId) {
        super("exception.framework.useragentrepeatexception", userId);
    }
}
