package codedriver.framework.dto;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.restful.annotation.EntityField;

/**
 * @program: codedriver
 * @description:
 * @create: 2019-12-09 16:47
 **/
public class MailServerVo extends BasePageVo {
	
	@EntityField(name = "uuid", type = ApiParamType.STRING)
    private String uuid;
	
	@EntityField(name = "名称", type = ApiParamType.STRING)
    private String name;
	
	@EntityField(name = "smtp端口", type = ApiParamType.INTEGER)
    private Integer port;
	
	@EntityField(name = "smtp主机", type = ApiParamType.STRING)
    private String host;
	
	@EntityField(name = "用户名", type = ApiParamType.STRING)
    private String userName;
	
	@EntityField(name = "密码", type = ApiParamType.STRING)
    private String password;
	
	@EntityField(name = "域名", type = ApiParamType.STRING)
    private String domain;
	
	@EntityField(name = "是否激活", type = ApiParamType.INTEGER)
    private int isActive;
	
	@EntityField(name = "邮箱地址", type = ApiParamType.EMAIL)
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
