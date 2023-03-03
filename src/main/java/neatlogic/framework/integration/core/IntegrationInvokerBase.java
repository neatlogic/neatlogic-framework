package neatlogic.framework.integration.core;

import org.springframework.util.ClassUtils;

import com.alibaba.fastjson.JSONObject;

import neatlogic.framework.integration.dto.IntegrationInvokeDetailVo;

public abstract class IntegrationInvokerBase<T> {
	private Object key;

	public abstract String getType();

	public final String getHandler() {
		return ClassUtils.getUserClass(this.getClass()).getName();
	}

	public JSONObject getConfig() {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("key", this.key);
		jsonObj.put("handler", this.getHandler());
		return jsonObj;
	}

	public IntegrationInvokerBase(Object _key) {
		this.key = _key;
	}

	public IntegrationInvokerBase() {

	}

	public abstract IntegrationInvokeDetailVo getInvokeDetail(T key);

}
