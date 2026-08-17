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

    default String[] getMethod() {
        return new String[]{"get", "post"};
    }

    IntegrationResultVo sendRequest(IntegrationVo integrationVo, IRequestFrom iRequestFrom);

    Integer hasPattern();

    /**
     * 输入参数是否使用处理器固定规范。
     * 默认兼容旧处理器的统一参数规范开关。
     */
    default Integer hasInputPattern() {
        return hasPattern();
    }

    /**
     * 输出参数是否使用处理器固定规范。
     * 默认兼容旧处理器的统一参数规范开关。
     */
    default Integer hasOutputPattern() {
        return hasPattern();
    }

    List<PatternVo> getInputPattern();

    List<PatternVo> getOutputPattern();

    void validate(IntegrationResultVo resultVo) throws ApiRuntimeException;

}
