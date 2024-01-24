package neatlogic.framework.exception.team;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class TeamNameRepeatException extends ApiRuntimeException {

    public TeamNameRepeatException(String name) {
        super("nfet.teamnamerepeatexception.teamnamerepeatexception", name);
    }
}
