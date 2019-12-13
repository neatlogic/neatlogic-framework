package codedriver.framework.restful.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class ApiAuditContentVo {

	private String tenantUuid;
	private String uuid;
	private JSONObject param;
	private String error;
	private Object result;
	
	public ApiAuditContentVo() {
	}
	public ApiAuditContentVo(String tenantUuid, String uuid, JSONObject param, String error, Object result) {
		this.tenantUuid = tenantUuid;
		this.uuid = uuid;
		this.param = param;
		this.error = error;
		this.result = result;
	}
	public String getTenantUuid() {
		return tenantUuid;
	}
	public void setTenantUuid(String tenantUuid) {
		this.tenantUuid = tenantUuid;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public JSONObject getParam() {
		return param;
	}
	public void setParam(JSONObject param) {
		this.param = param;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	
	/**
	 * 
	* @Description: 接口日志格式化
	* @param uuid 对应api_audit表的uuid
	* @param param 请求参数
	* @param error 处理异常信息
	* @param result 请求返回结果
	* @return String
	 */
	public String logFormat() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		StringBuilder log = new StringBuilder();
		log.append("\n");
		log.append(">>>\tuuid: ");
		log.append(uuid);
		log.append("\n");
		log.append("\ttime: ");
		log.append(sdf.format(new Date()));
		log.append("\n");
		log.append("\tparam: ");
		log.append(param);
		log.append("\n");
		if(StringUtils.isNotBlank(error)) {
			log.append("\terror: ");
			log.append(error);
		}else {
			log.append("\tresult: ");
			String pretty = JSON.toJSONString(result, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
			log.append(pretty);			
		}
		log.append("\n");
		log.append("<<<");
		return log.toString();
	}
}
