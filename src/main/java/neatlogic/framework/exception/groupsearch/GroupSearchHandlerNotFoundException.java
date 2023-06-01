package neatlogic.framework.exception.groupsearch;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class GroupSearchHandlerNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 4778633677540696671L;

    public GroupSearchHandlerNotFoundException(String handler) {
        super("找不到类型为：{0}的仪表板组件", handler);
    }
}
