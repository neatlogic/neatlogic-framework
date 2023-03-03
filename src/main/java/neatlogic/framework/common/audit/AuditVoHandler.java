package neatlogic.framework.common.audit;

public interface AuditVoHandler {

	String getParam();

	void setParam(String param);

	Object getResult();

	void setResult(Object result);

	String getError();

	void setError(String error);

	String getParamFilePath();

	void setParamFilePath(String paramFilePath);

	String getResultFilePath();

	void setResultFilePath(String resultFilePath);

	String getErrorFilePath();

	void setErrorFilePath(String errorFilePath);

}
