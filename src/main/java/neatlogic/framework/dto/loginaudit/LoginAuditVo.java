package neatlogic.framework.dto.loginaudit;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoginAuditVo implements Serializable {
    @Serial
    private static final long serialVersionUID = -977868375722886386L;
    private Long id;
    private String userUuid;
    private String ip;
    private Date loginTime;
    private String loginMethod;
    private List<String> teamNameList = new ArrayList<>();

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

    public List<String> getTeamNameList() {
        return teamNameList;
    }

    public void setTeamNameList(List<String> teamNameList) {
        this.teamNameList = teamNameList;
    }
}
