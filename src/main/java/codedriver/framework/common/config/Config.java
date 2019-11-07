package codedriver.framework.common.config;

public class Config {
	public static String REST_AUDIT_PATH;
	public static final String RESPONSE_TYPE_JSON = "application/json;charset=UTF-8";
	public static final String RESPONSE_TYPE_HTML = "text/html;charset=UTF-8";
	public static final String RESPONSE_TYPE_TEXT = "text/plain;charset=UTF-8";
	static {
		REST_AUDIT_PATH = "/app/codedriver/";
	}
}
