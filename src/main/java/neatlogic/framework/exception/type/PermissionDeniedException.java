package neatlogic.framework.exception.type;

import neatlogic.framework.auth.core.AuthBase;
import neatlogic.framework.auth.core.AuthFactory;
import neatlogic.framework.exception.core.ApiException;
import neatlogic.framework.util.$;

import java.util.List;

public class PermissionDeniedException extends ApiException {

    /**
     * @Fields serialVersionUID : TODO
     */
    private static final long serialVersionUID = 6148939003449322484L;

    public PermissionDeniedException() {
        super("没有权限执行该操作，请联系管理员");
    }
    public PermissionDeniedException(String message) {
        super(message);
    }
    public PermissionDeniedException(String key, Object ... values) {
        super(key, values);
    }
    public PermissionDeniedException(Class<? extends AuthBase> authClass) {
        super("当前用户缺少“{0}”权限，请联系管理员", $.t(AuthFactory.getAuthInstance(authClass.getSimpleName()).getAuthDisplayName()));
    }

    public PermissionDeniedException(List<String> authNameList) {
        super("当前用户缺少“{0}”权限，请联系管理员", String.join("、", authNameList));
    }

}
