package neatlogic.framework.exception.user;

import neatlogic.framework.exception.core.ApiRuntimeException;

@Deprecated
public class UserAgentLoopException extends ApiRuntimeException {

    private static final long serialVersionUID = 7559156456698135295L;

    @Deprecated
    public UserAgentLoopException(String userId, String agentId) {
        super("用户：{1}已代理{0}，不可循环代理", agentId, userId);
    }
}
