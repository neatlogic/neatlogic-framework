package neatlogic.framework.exception.team;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class TeamNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 1898956131442742449L;

    public TeamNotFoundException(String teamUuid) {
        super("分组：{0}不存在", teamUuid);
    }
}
