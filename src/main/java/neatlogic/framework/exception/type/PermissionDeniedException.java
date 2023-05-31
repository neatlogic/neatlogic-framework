package neatlogic.framework.exception.type;

import neatlogic.framework.auth.core.AuthBase;
import neatlogic.framework.auth.core.AuthFactory;
import neatlogic.framework.exception.core.ApiException;
import neatlogic.framework.util.I18n;

import java.util.List;

public class PermissionDeniedException extends ApiException {

    /**
     * @Fields serialVersionUID : TODO
     */
    private static final long serialVersionUID = 6148939003449322484L;

    public PermissionDeniedException() {
        super("exception.framework.permissiondeniedexception.a");
    }
    public PermissionDeniedException(String message) {
        super(message);
    }
    public PermissionDeniedException(String key, Object ... values) {
        super(key, values);
    }
    public PermissionDeniedException(Class<? extends AuthBase> authClass) {
        super("exception.lackauth", new I18n(AuthFactory.getAuthInstance(authClass.getSimpleName()).getAuthDisplayName()).toString());
    }

    public PermissionDeniedException(List<String> authNameList) {
        super("exception.lackauth", String.join("、", authNameList));
    }

}
