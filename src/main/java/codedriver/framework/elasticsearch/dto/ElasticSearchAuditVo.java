package codedriver.framework.elasticsearch.dto;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

public class ElasticSearchAuditVo {

	private String handler;
	private String param;
	private String paramHash;
	private Date udpateTime;
	
	public ElasticSearchAuditVo() {

	}

	public ElasticSearchAuditVo(String handler,String param) {
		this.handler = handler;
		this.param = param;
	}

	
	public String getHandler() {
		return handler;
	}
	public void setHandler(String handler) {
		this.handler = handler;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	public Date getUdpateTime() {
		return udpateTime;
	}
	public void setUdpateTime(Date udpateTime) {
		this.udpateTime = udpateTime;
	}

    public String getParamHash() {
        if(StringUtils.isBlank(paramHash)) {
            paramHash = DigestUtils.md5DigestAsHex(param.getBytes());
        }
        return paramHash;
    }

    public void setParamHash(String paramHash) {
        this.paramHash = paramHash;
    }
	
	
}
