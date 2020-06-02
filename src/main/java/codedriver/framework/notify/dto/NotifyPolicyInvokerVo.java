package codedriver.framework.notify.dto;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class NotifyPolicyInvokerVo {

	private Long policyId;
	private String invoker;
	private String config;
	private JSONObject configObj;
	private String function;
	private String name;
	
	public Long getPolicyId() {
		return policyId;
	}
	public void setPolicyId(Long policyId) {
		this.policyId = policyId;
	}
	public String getInvoker() {
		return invoker;
	}
	public void setInvoker(String invoker) {
		this.invoker = invoker;
	}
	public String getConfig() {
		if(StringUtils.isBlank(config)) {
			Map<String, Object> configMap = new HashMap<>();
			if(StringUtils.isNotBlank(function)) {
				configMap.put("function", function);
			}
			if(StringUtils.isNotBlank(name)) {
				configMap.put("name", name);
			}
			if(MapUtils.isNotEmpty(configMap)) {
				config = JSON.toJSONString(configMap);
			}
		}
		return config;
	}
	public void setConfig(String config) {
		this.config = config;
	}
	public JSONObject getConfigObj() {
		if(configObj == null && StringUtils.isNotBlank(config)) {
			configObj = JSON.parseObject(config);
		}
		return configObj;
	}
	public void setConfigObj(JSONObject configObj) {
		this.configObj = configObj;
	}
	public String getFunction() {
		if(StringUtils.isBlank(function) && MapUtils.isNotEmpty(getConfigObj())) {
			function = getConfigObj().getString("function");
		}
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}
	public String getName() {
		if(StringUtils.isBlank(name) && MapUtils.isNotEmpty(getConfigObj())) {
			name = getConfigObj().getString("name");
		}
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
