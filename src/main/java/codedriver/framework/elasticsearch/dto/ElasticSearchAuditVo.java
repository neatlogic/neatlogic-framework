package codedriver.framework.elasticsearch.dto;

import java.util.Date;

public class ElasticSearchAuditVo {

	private Long id;
	private String handler;
	private String config;
	private Date udpateTime;
	
	public ElasticSearchAuditVo() {

	}

	public ElasticSearchAuditVo(String handler, String config) {
		this.handler = handler;
		this.config = config;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getHandler() {
		return handler;
	}
	public void setHandler(String handler) {
		this.handler = handler;
	}
	public String getConfig() {
		return config;
	}
	public void setConfig(String config) {
		this.config = config;
	}
	public Date getUdpateTime() {
		return udpateTime;
	}
	public void setUdpateTime(Date udpateTime) {
		this.udpateTime = udpateTime;
	}
	
	
}
