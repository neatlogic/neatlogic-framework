package neatlogic.framework.dto.loginaudit;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

public class LoginAuditVo implements Serializable {
    @Serial
    private static final long serialVersionUID = -977868375722886386L;
    private Long id;
    private String userUuid;
    private String ip;
    private Date loginTime;
    private String loginMethod;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public String getLoginMethod() {
        return loginMethod;
    }

    public void setLoginMethod(String loginMethod) {
        this.loginMethod = loginMethod;
    }
}
