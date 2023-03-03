package neatlogic.framework.exception.elasticsearch;

public class ElatsticSearchHandlerNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 8358695524151979636L;

	public ElatsticSearchHandlerNotFoundException(String handler) {
		super("找不到类型为：" + handler + "的工单中心处理器");
	}
}
