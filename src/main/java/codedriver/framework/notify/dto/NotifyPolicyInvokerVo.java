package codedriver.framework.notify.dto;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import codedriver.framework.common.dto.BasePageVo;

public class NotifyPolicyInvokerVo extends BasePageVo {

	private Long policyId;
	private String invoker;
	private JSONObject config;
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
	public JSONObject getConfig() {
		if(MapUtils.isEmpty(config)) {
			JSONObject configObj = new JSONObject();
			if(StringUtils.isNotBlank(function)) {
				configObj.put("function", function);
			}
			if(StringUtils.isNotBlank(name)) {
				configObj.put("name", name);
			}
			if(MapUtils.isNotEmpty(configObj)) {
				config = configObj;
			}
		}
		return config;
	}
	public void setConfig(String config) {
		try {
			this.config = JSONObject.parseObject(config);
		} catch (Exception ex) {

		}
	}
	
	@JSONField(serialize = false)
	public String getConfigStr() {
		if (config != null) {
			return config.toJSONString();
		}
		return null;
	}
	
	public String getFunction() {
		if(StringUtils.isBlank(function) && MapUtils.isNotEmpty(config)) {
			function = config.getString("function");
		}
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}
	public String getName() {
		if(StringUtils.isBlank(name) && MapUtils.isNotEmpty(config)) {
			name = config.getString("name");
		}
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
