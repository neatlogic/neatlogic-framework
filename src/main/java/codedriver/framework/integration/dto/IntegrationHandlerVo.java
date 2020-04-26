package codedriver.framework.integration.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.integration.core.IIntegrationHandler;
import codedriver.framework.integration.core.IntegrationHandlerFactory;
import codedriver.framework.restful.annotation.EntityField;

public class IntegrationHandlerVo {
	@EntityField(name = "名称", type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "处理器", type = ApiParamType.STRING)
	private String handler;
	@EntityField(name = "输入参数模板", type = ApiParamType.JSONARRAY)
	private List<PatternVo> inputPatternList = new ArrayList<>();
	@EntityField(name = "输出参数模板", type = ApiParamType.JSONARRAY)
	private List<PatternVo> outputPatternList = new ArrayList<>();

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

	public List<PatternVo> getInputPatternList() {
		if ((inputPatternList == null || inputPatternList.size() == 0) && StringUtils.isNotBlank(handler)) {
			IIntegrationHandler<?> integrationHandler = IntegrationHandlerFactory.getHandler(handler);
			if (integrationHandler != null) {
				inputPatternList = integrationHandler.getInputPatternList();
			}
		}
		return inputPatternList;
	}

	public List<PatternVo> getOutputPatternList() {
		if ((outputPatternList == null || outputPatternList.size() == 0) && StringUtils.isNotBlank(handler)) {
			IIntegrationHandler<?> integrationHandler = IntegrationHandlerFactory.getHandler(handler);
			if (integrationHandler != null) {
				outputPatternList = integrationHandler.getOutputPatternList();
			}
		}
		return outputPatternList;
	}

}
