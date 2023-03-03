package neatlogic.framework.integration.dto;

import com.alibaba.fastjson.JSONObject;

import neatlogic.framework.integration.core.IntegrationInvokerBase;
import neatlogic.framework.integration.core.IntegrationInvokerFactory;
import neatlogic.framework.util.Md5Util;

public class IntegrationInvokeDetailVo {
	private String type;
	private String name;
	private String url;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
