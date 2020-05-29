package codedriver.framework.integration.core;

import java.util.List;

import org.springframework.util.ClassUtils;

import codedriver.framework.integration.dto.IntegrationResultVo;
import codedriver.framework.integration.dto.IntegrationVo;
import codedriver.framework.integration.dto.PatternVo;

public interface IIntegrationHandler {
	public String getName();

	public default String getHandler() {
		return ClassUtils.getUserClass(this.getClass()).getName();
	}

	public IntegrationResultVo sendRequest(IntegrationVo integrationVo);
	
	public Integer hasPattern();

	public List<PatternVo> getInputPattern();

	public List<PatternVo> getOutputPattern();

}
