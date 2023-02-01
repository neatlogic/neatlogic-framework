package neatlogic.framework.integration.core;

import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.integration.dto.IntegrationResultVo;
import neatlogic.framework.integration.dto.IntegrationVo;
import neatlogic.framework.integration.dto.PatternVo;
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
