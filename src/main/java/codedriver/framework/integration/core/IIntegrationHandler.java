package codedriver.framework.integration.core;

import org.springframework.util.ClassUtils;

import com.alibaba.fastjson.JSONArray;

import codedriver.framework.integration.dto.IntegrationVo;

public interface IIntegrationHandler<T> {
	public String getName();

	public default String getHandler() {
		return ClassUtils.getUserClass(this.getClass()).getName();
	}

	public Boolean allowCustomPattern();

	public T getData(IntegrationVo integrationVo);

	public JSONArray getInputPattern();

	public JSONArray getOutputPattern();

}
