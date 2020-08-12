package codedriver.framework.common.audit;

public interface AuditVoHandler {

	public String getParam();

	public void setParam(String param);

	public Object getResult();

	public void setResult(Object result);

	public String getError();

	public void setError(String error);

	public String getParamFilePath();

	public void setParamFilePath(String paramFilePath);

	public String getResultFilePath();

	public void setResultFilePath(String resultFilePath);

	public String getErrorFilePath();

	public void setErrorFilePath(String errorFilePath);

}
