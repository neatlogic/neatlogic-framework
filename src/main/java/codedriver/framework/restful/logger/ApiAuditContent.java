package codedriver.framework.restful.logger;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class ApiAuditContent implements Content{

	private String tenantUuid;
	private String uuid;
	private JSONObject param;
	private String error;
	private Object result;
	
	public ApiAuditContent() {
	}
	public ApiAuditContent(String tenantUuid, String uuid, JSONObject param, String error, Object result) {
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
	* @Description: 内容格式化
	* @return String
	 */
	@Override
	public String format() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		StringBuilder log = new StringBuilder();
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
		log.append("<<<\n");
		return log.toString();
	}
}
