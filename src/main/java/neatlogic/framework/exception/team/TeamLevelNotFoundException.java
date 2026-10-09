package neatlogic.framework.exception.team;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class TeamLevelNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = -6608670031156139058L;

    /**
     * 创建组织层级不存在异常。
     *
     * @param level 不存在的组织层级值
     */
    public TeamLevelNotFoundException(String level) {
        super("nfet.teamlevelnotfoundexception.teamlevelnotfoundexception", level);
    }
}
