package neatlogic.framework.exception.type;

import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.auth.core.AuthBase;
import neatlogic.framework.auth.core.AuthFactory;
import neatlogic.framework.exception.core.ApiException;
import neatlogic.framework.util.$;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/** 当前用户没有操作权限时返回统一的拒绝提示。 */
public class PermissionDeniedException extends ApiException {

    /**
     * @Fields serialVersionUID : TODO
     */
    private static final long serialVersionUID = 6148939003449322484L;

    /** 返回通用权限拒绝提示，能够识别当前用户时附带用户信息。 */
    public PermissionDeniedException() {
        this(buildDefaultMessage());
    }

    public PermissionDeniedException(String message) {
        super(message);
    }

    public PermissionDeniedException(String key, Object ... values) {
        super(key, values);
    }

    /** 返回单项权限拒绝提示，能够识别当前用户时附带用户信息。 */
    public PermissionDeniedException(Class<? extends AuthBase> authClass) {
        this(buildRequiredPermissionMessage(
                $.t(AuthFactory.getAuthInstance(authClass.getSimpleName()).getAuthDisplayName())));
    }

    /** 返回权限列表拒绝提示，能够识别当前用户时附带用户信息。 */
    public PermissionDeniedException(List<String> authNameList) {
        this(buildRequiredPermissionMessage(String.join("、", authNameList)));
    }

    /** 使用标准权限提示创建异常，集中处理模板及其参数。 */
    private PermissionDeniedException(PermissionMessage message) {
        super(message.template(), message.args());
    }

    /** 创建通用权限拒绝提示，无法识别当前用户时不展示用户信息。 */
    private static PermissionMessage buildDefaultMessage() {
        String currentUser = getCurrentUserDisplayName();
        if (currentUser == null) {
            return new PermissionMessage("没有权限执行该操作，请联系管理员", new Object[0]);
        }
        return new PermissionMessage("当前用户“{0}”没有权限执行该操作，请联系管理员",
                new Object[]{currentUser});
    }

    /** 创建缺少指定权限的提示，无法识别当前用户时沿用不带身份的提示。 */
    private static PermissionMessage buildRequiredPermissionMessage(String authName) {
        String currentUser = getCurrentUserDisplayName();
        if (currentUser == null) {
            return new PermissionMessage("当前用户缺少“{0}”权限，请联系管理员", new Object[]{authName});
        }
        return new PermissionMessage("当前用户“{0}”缺少“{1}”权限，请联系管理员",
                new Object[]{currentUser, authName});
    }

    /** 获取当前用户的可识别名称，用户信息不可用时返回 null。 */
    private static String getCurrentUserDisplayName() {
        UserContext userContext = UserContext.get();
        if (userContext == null) {
            return null;
        }
        String userName = StringUtils.trimToNull(userContext.getUserName());
        String userId = StringUtils.trimToNull(userContext.getUserId());
        if (userName != null && userId != null) {
            return userName + "(" + userId + ")";
        }
        return StringUtils.defaultIfBlank(userName, userId);
    }

    /** 标准权限提示及其格式化参数。 */
    private record PermissionMessage(String template, Object[] args) {
    }

}
