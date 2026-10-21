package neatlogic.framework.integration.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.integration.core.IIntegrationHandler;
import neatlogic.framework.integration.core.IntegrationHandlerFactory;
import neatlogic.framework.restful.annotation.EntityField;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class IntegrationHandlerVo {
	@EntityField(name = "common.name", type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "nfid.integrationhandlervo.entityfield.name.handler", type = ApiParamType.STRING)
	private String handler;
	@EntityField(name = "nfid.integrationhandlervo.entityfield.name.inputpattern", type = ApiParamType.JSONARRAY)
	private List<PatternVo> inputPattern;
	@EntityField(name = "nfid.integrationhandlervo.entityfield.name.outputpattern", type = ApiParamType.JSONARRAY)
	private List<PatternVo> outputPattern;
	@EntityField(name = "nfid.integrationhandlervo.entityfield.name.haspattern", type = ApiParamType.INTEGER)
	private Integer hasPattern;
	@EntityField(name = "nfid.integrationhandlervo.entityfield.name.hasinputpattern", type = ApiParamType.INTEGER)
	private Integer hasInputPattern;
	@EntityField(name = "nfid.integrationhandlervo.entityfield.name.hasoutputpattern", type = ApiParamType.INTEGER)
	private Integer hasOutputPattern;
	@EntityField(name = "nfid.integrationhandlervo.entityfield.name.alloweditinputparam", type = ApiParamType.INTEGER)
	private Integer allowEditInputParam;
	@EntityField(name = "nfid.integrationhandlervo.entityfield.name.alloweditoutputparam", type = ApiParamType.INTEGER)
	private Integer allowEditOutputParam;
	@EntityField(name = "nfid.integrationhandlervo.entityfield.name.methodlist", type = ApiParamType.JSONARRAY)
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
