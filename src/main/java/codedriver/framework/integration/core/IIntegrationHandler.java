package codedriver.framework.integration.core;

import codedriver.framework.exception.core.ApiRuntimeException;
import codedriver.framework.integration.dto.IntegrationResultVo;
import codedriver.framework.integration.dto.IntegrationVo;
import codedriver.framework.integration.dto.PatternVo;
import org.springframework.util.ClassUtils;

import java.util.List;

public interface IIntegrationHandler {

	public String getName();

	public default String getHandler() {
		return ClassUtils.getUserClass(this.getClass()).getSimpleName();
	}

	public IntegrationResultVo sendRequest(IntegrationVo integrationVo, IRequestFrom iRequestFrom);

	public Integer hasPattern();

	public List<PatternVo> getInputPattern();

	public List<PatternVo> getOutputPattern();

	public void validate(IntegrationResultVo resultVo) throws ApiRuntimeException;

}
