package codedriver.framework.dto;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import codedriver.framework.common.dto.BasePageVo;

/**
 * @program: codedriver
 * @description:
 * @create: 2019-12-09 16:47
 **/
public class MailServerVo extends BasePageVo {
    private String uuid;
    private String name;
    private Integer port;
    private String host;
    private String userName;
    private String password;
    private String domain;
    private int isActive;
    private String fromAddress;

    private transient String keyword;
    
    public String getUuid() {
    	if(StringUtils.isBlank(uuid)) {
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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
}
