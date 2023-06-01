package neatlogic.framework.exception.team;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class TeamLevelNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = -6608670031156139058L;

    public TeamLevelNotFoundException(String level) {
        super("组层级：“{0}”不存在", level);
    }
}
