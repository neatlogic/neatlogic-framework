package neatlogic.framework.exception.type;

import neatlogic.framework.auth.core.AuthBase;
import neatlogic.framework.auth.core.AuthFactory;
import neatlogic.framework.exception.core.ApiException;

import java.util.List;

public class PermissionDeniedException extends ApiException {

    /**
     * @Fields serialVersionUID : TODO
     */
    private static final long serialVersionUID = 6148939003449322484L;

    public PermissionDeniedException() {
        super("exception.framework.permissiondeniedexception");
    }
    public PermissionDeniedException(String message) {
        super(message);
    }
    public PermissionDeniedException(String key, Object ... values) {
        super(key, values);
    }
    public PermissionDeniedException(Class<? extends AuthBase> authClass) {
        super("exception.framework.permissiondeniedexception.1", AuthFactory.getAuthInstance(authClass.getSimpleName()).getAuthDisplayName());
    }

    public PermissionDeniedException(List<String> authNameList) {
        super("exception.framework.permissiondeniedexception.2", String.join("、", authNameList));
    }

}
