package codedriver.framework.integration.authentication.costvalue;

public enum HttpMethod {
	GET("get"), POST("post");

	private String type;

	private HttpMethod(String _type) {
		this.type = _type;
	}

	public String toString() {
		return this.type;
	}
}
