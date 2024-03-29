package neatlogic.framework.exception.team;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class FoundRepeatNameTeamException extends ApiRuntimeException {

    private static final long serialVersionUID = -7791153993204463500L;

    public FoundRepeatNameTeamException(String name) {
        super("存在多个名为：{0}的分组", name);
    }
}
