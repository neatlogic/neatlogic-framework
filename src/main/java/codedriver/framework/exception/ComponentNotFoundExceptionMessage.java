package codedriver.framework.exception;

public class ComponentNotFoundExceptionMessage extends FrameworkExceptionMessageBase {
	private String componentId;

	public ComponentNotFoundExceptionMessage(String _componentId) {
		componentId = _componentId;
	}

	@Override
	public String getError() {
		return "插件：" + componentId + "不存在";
	}

	@Override
	protected String myGetErrorCode() {
		return "02";
	}

}
