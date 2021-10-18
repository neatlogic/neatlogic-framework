package codedriver.framework.dto.runner;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;
import codedriver.framework.util.SnowflakeUtil;

public class RunnerAuthVo {

    private static final long serialVersionUID = -5318993385455680706L;
    @EntityField(name = "id", type = ApiParamType.LONG)
    private Long id;
    @EntityField(name = "runner 授权类型", type = ApiParamType.STRING)
    private String authType;
    @EntityField(name = "runner 授权key", type = ApiParamType.STRING)
    private String accessKey;
    @EntityField(name = "runner 授权密码", type = ApiParamType.STRING)
    private String accessSecret;
    @EntityField(name = "runner id", type = ApiParamType.LONG)
    private Long runnerId;

    public Long getId() {
        if (id == null) {
            id = SnowflakeUtil.uniqueLong();
        }
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
    }

    public Long getRunnerId() {
        return runnerId;
    }

    public void setRunnerId(Long runnerId) {
        this.runnerId = runnerId;
    }
}
