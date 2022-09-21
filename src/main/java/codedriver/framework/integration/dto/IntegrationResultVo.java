package codedriver.framework.integration.dto;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;
import org.apache.commons.lang3.StringUtils;

public class IntegrationResultVo {
	private int statusCode;
	private StringBuffer resultBuffer;
	private String transformedResult;
	private StringBuffer errorBuffer;
	private String transformedParam;
	@EntityField(name = "集成记录Id", type = ApiParamType.LONG)
	private Long auditId;

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getRawResult() {
		if (resultBuffer != null) {
			return resultBuffer.toString().trim();
		}
		return null;
	}

	public String getError() {
		if (errorBuffer != null) {
			return errorBuffer.toString().trim();
		}
		return null;
	}

	public void appendResult(String resultline) {
		if (StringUtils.isNotBlank(resultline)) {
			if (resultBuffer == null) {
				resultBuffer = new StringBuffer();
			}
			resultBuffer.append(resultline);
		}
	}

	public void appendError(String resultline) {
		if (StringUtils.isNotBlank(resultline)) {
			if (errorBuffer == null) {
				errorBuffer = new StringBuffer();
			}
			errorBuffer.append(resultline);
		}
	}

	public String getTransformedResult() {
		return transformedResult;
	}

	public void setTransformedResult(String transformedResult) {
		this.transformedResult = transformedResult;
	}

	public String getTransformedParam() {
		return transformedParam;
	}

	public void setTransformedParam(String transformedParam) {
		this.transformedParam = transformedParam;
	}

	public Long getAuditId() {
		return auditId;
	}

	public void setAuditId(Long auditId) {
		this.auditId = auditId;
	}
}
