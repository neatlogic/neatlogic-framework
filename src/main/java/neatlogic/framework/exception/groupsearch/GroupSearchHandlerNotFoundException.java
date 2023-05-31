package neatlogic.framework.exception.groupsearch;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class GroupSearchHandlerNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 4778633677540696671L;

    public GroupSearchHandlerNotFoundException(String handler) {
        super("exception.dashboardhandlernotfound", handler);
    }
}
