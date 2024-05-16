package neatlogic.framework.integration.core;

import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.integration.dto.IntegrationResultVo;
import neatlogic.framework.integration.dto.IntegrationVo;
import neatlogic.framework.integration.dto.PatternVo;
import org.springframework.util.ClassUtils;

import java.util.List;

public interface IIntegrationHandler {

    String getName();

    default String getHandler() {
        return ClassUtils.getUserClass(this.getClass()).getSimpleName();
    }

    IntegrationResultVo sendRequest(IntegrationVo integrationVo, IRequestFrom iRequestFrom);

    Integer hasPattern();

    List<PatternVo> getInputPattern();

    List<PatternVo> getOutputPattern();

    void validate(IntegrationResultVo resultVo) throws ApiRuntimeException;

}
