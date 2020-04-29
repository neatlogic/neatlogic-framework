package codedriver.framework.integration.core;

import org.springframework.util.ClassUtils;

import com.alibaba.fastjson.JSONArray;

import codedriver.framework.integration.dto.IntegrationResultVo;
import codedriver.framework.integration.dto.IntegrationVo;

public interface IIntegrationHandler {
	public String getName();

	public default String getHandler() {
		return ClassUtils.getUserClass(this.getClass()).getName();
	}

	public IntegrationResultVo sendRequest(IntegrationVo integrationVo);

	public JSONArray getInputPattern();

	public JSONArray getOutputPattern();

}
