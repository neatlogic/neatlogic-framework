package neatlogic.framework.integration.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.integration.core.IIntegrationHandler;
import neatlogic.framework.integration.core.IntegrationHandlerFactory;
import neatlogic.framework.restful.annotation.EntityField;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class IntegrationHandlerVo {
	@EntityField(name = "名称", type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "处理器", type = ApiParamType.STRING)
	private String handler;
	@EntityField(name = "输入参数模板", type = ApiParamType.JSONARRAY)
	private List<PatternVo> inputPattern;
	@EntityField(name = "输出参数模板", type = ApiParamType.JSONARRAY)
	private List<PatternVo> outputPattern;
	@EntityField(name = "是否拥有参数模板", type = ApiParamType.INTEGER)
	private Integer hasPattern;
	@EntityField(name = "是否拥有输入参数模板", type = ApiParamType.INTEGER)
	private Integer hasInputPattern;
	@EntityField(name = "是否拥有输出参数模板", type = ApiParamType.INTEGER)
	private Integer hasOutputPattern;
	@EntityField(name = "是否允许编辑自定义输入参数", type = ApiParamType.INTEGER)
	private Integer allowEditInputParam;
	@EntityField(name = "是否允许编辑自定义输出参数", type = ApiParamType.INTEGER)
	private Integer allowEditOutputParam;
	@EntityField(name = "支持的请求方式", type = ApiParamType.JSONARRAY)
	private String[] methodList;

	@SuppressWarnings("unused")
	private IntegrationHandlerVo() {

	}

	public IntegrationHandlerVo(String _name, String _handler) {
		name = _name;
		handler = _handler;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}

	public List<PatternVo> getInputPattern() {
		if (inputPattern == null && StringUtils.isNotBlank(handler)) {
			IIntegrationHandler integrationHandler = IntegrationHandlerFactory.getHandler(handler);
			if (integrationHandler != null) {
				inputPattern = integrationHandler.getInputPattern();
			}
		}
		return inputPattern;
	}

	public List<PatternVo> getOutputPattern() {
		if (outputPattern == null && StringUtils.isNotBlank(handler)) {
			IIntegrationHandler integrationHandler = IntegrationHandlerFactory.getHandler(handler);
			if (integrationHandler != null) {
				outputPattern = integrationHandler.getOutputPattern();
			}
		}
		return outputPattern;
	}

	public Integer getHasPattern() {
		if (hasPattern == null && StringUtils.isNotBlank(handler)) {
			IIntegrationHandler integrationHandler = IntegrationHandlerFactory.getHandler(handler);
			if (integrationHandler != null) {
				hasPattern = integrationHandler.hasPattern();
			}
		}
		return hasPattern;
	}

	public void setHasPattern(Integer hasPattern) {
		this.hasPattern = hasPattern;
	}

	public Integer getHasInputPattern() {
		if (hasInputPattern == null && StringUtils.isNotBlank(handler)) {
			IIntegrationHandler integrationHandler = IntegrationHandlerFactory.getHandler(handler);
			if (integrationHandler != null) {
				hasInputPattern = integrationHandler.hasInputPattern();
			}
		}
		return hasInputPattern;
	}

	public void setHasInputPattern(Integer hasInputPattern) {
		this.hasInputPattern = hasInputPattern;
	}

	public Integer getHasOutputPattern() {
		if (hasOutputPattern == null && StringUtils.isNotBlank(handler)) {
			IIntegrationHandler integrationHandler = IntegrationHandlerFactory.getHandler(handler);
			if (integrationHandler != null) {
				hasOutputPattern = integrationHandler.hasOutputPattern();
			}
		}
		return hasOutputPattern;
	}

	public void setHasOutputPattern(Integer hasOutputPattern) {
		this.hasOutputPattern = hasOutputPattern;
	}

	public String[] getMethodList() {
		if (methodList == null && StringUtils.isNotBlank(handler)) {
			IIntegrationHandler integrationHandler = IntegrationHandlerFactory.getHandler(handler);
			if (integrationHandler != null) {
				methodList = integrationHandler.getMethod();
			}
		}
		return methodList;
	}

	/** 查询和列表接口都从处理器读取编辑权限，不再通过是否有固定规范推断。 */
	public Integer getAllowEditInputParam() {
		if (allowEditInputParam == null && StringUtils.isNotBlank(handler)) {
			IIntegrationHandler integrationHandler = IntegrationHandlerFactory.getHandler(handler);
			if (integrationHandler != null) {
				allowEditInputParam = integrationHandler.allowEditInputParam();
			}
		}
		return allowEditInputParam;
	}

	public void setAllowEditInputParam(Integer allowEditInputParam) {
		this.allowEditInputParam = allowEditInputParam;
	}

	/** 输出编辑权限单独返回，以支持只开放一个方向的处理器。 */
	public Integer getAllowEditOutputParam() {
		if (allowEditOutputParam == null && StringUtils.isNotBlank(handler)) {
			IIntegrationHandler integrationHandler = IntegrationHandlerFactory.getHandler(handler);
			if (integrationHandler != null) {
				allowEditOutputParam = integrationHandler.allowEditOutputParam();
			}
		}
		return allowEditOutputParam;
	}

	public void setAllowEditOutputParam(Integer allowEditOutputParam) {
		this.allowEditOutputParam = allowEditOutputParam;
	}

	public void setMethodList(String[] methodList) {
		this.methodList = methodList;
	}

}
