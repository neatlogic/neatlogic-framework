package codedriver.framework.exception.type;

import codedriver.framework.auth.core.AuthBase;
import codedriver.framework.auth.core.AuthFactory;
import codedriver.framework.exception.core.ApiException;

import java.util.List;

public class PermissionDeniedException extends ApiException {

    /**
     * @Fields serialVersionUID : TODO
     */
    private static final long serialVersionUID = 6148939003449322484L;

    public PermissionDeniedException() {
        super("没有权限进行当前操作");
    }
    public PermissionDeniedException(String message) {
        super(message);
    }
    public PermissionDeniedException(Class<? extends AuthBase> authClass) {
        super("没有权限进行当前操作，请联系管理员确认是否拥有[" + AuthFactory.getAuthInstance(authClass.getSimpleName()).getAuthDisplayName() + "]");
    }

    public PermissionDeniedException(List<String> authNameList) {
        super("没有权限进行当前操作，请联系管理员确认是否拥有[" + String.join("、", authNameList) + "]");
    }

}
