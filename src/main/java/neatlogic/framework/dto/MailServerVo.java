package neatlogic.framework.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.restful.annotation.EntityField;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * @program: neatlogic
 * @description:
 * @create: 2019-12-09 16:47
 **/
public class MailServerVo extends BasePageVo {

    private static final long serialVersionUID = 6550790567158161414L;

    @EntityField(name = "common.uuid", type = ApiParamType.STRING)
    private String uuid;

    @EntityField(name = "common.name", type = ApiParamType.STRING)
    private String name;

    @EntityField(name = "term.framework.smptport", type = ApiParamType.INTEGER)
    private Integer port;

    @EntityField(name = "term.framework.smpthost", type = ApiParamType.STRING)
    private String host;

    @EntityField(name = "common.username", type = ApiParamType.STRING)
    private String userName;

    @EntityField(name = "common.password", type = ApiParamType.STRING)
    private String password;

    @EntityField(name = "common.mailaddress", type = ApiParamType.EMAIL)
    private String fromAddress;

    @EntityField(name = "term.framework.smptsslenable", type = ApiParamType.STRING)
    private String sslEnable = "false";

    @EntityField(name = "common.homeurl", type = ApiParamType.STRING)
    private String homeUrl;

    public String getUuid() {
        if (StringUtils.isBlank(uuid)) {
            uuid = UUID.randomUUID().toString().replace("-", "");
        }
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getSslEnable() {
        return sslEnable;
    }

    public void setSslEnable(String sslEnable) {
        this.sslEnable = sslEnable;
    }

    public String getHomeUrl() {
        return homeUrl;
    }

    public void setHomeUrl(String homeUrl) {
        this.homeUrl = homeUrl;
    }
}
