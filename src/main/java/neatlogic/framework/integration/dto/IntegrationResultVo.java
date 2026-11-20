package neatlogic.framework.integration.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;
import org.apache.commons.lang3.StringUtils;

public class IntegrationResultVo {
	private int statusCode;
	private Integer rawStatusCode;
	private String statusCodeTransformDetail;
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

	/** 返回从第三方实际收到的 HTTP 状态码，未收到响应时为空。 */
	public Integer getRawStatusCode() {
		return rawStatusCode;
	}

	/** 保存状态码转换前的 HTTP 状态码。 */
	public void setRawStatusCode(Integer rawStatusCode) {
		this.rawStatusCode = rawStatusCode;
	}

	/** 返回本次状态码转换的匹配或兜底说明。 */
	public String getStatusCodeTransformDetail() {
		return statusCodeTransformDetail;
	}

	/** 记录从响应字段得出生效状态码的原因。 */
	public void setStatusCodeTransformDetail(String statusCodeTransformDetail) {
		this.statusCodeTransformDetail = statusCodeTransformDetail;
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
