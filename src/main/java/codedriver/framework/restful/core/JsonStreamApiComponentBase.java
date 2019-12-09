package codedriver.framework.restful.core;

import java.lang.reflect.Method;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.common.config.Config;
import codedriver.framework.common.util.IpUtil;
import codedriver.framework.restful.dao.mapper.ApiMapper;
import codedriver.framework.restful.dto.ApiAuditVo;
import codedriver.framework.restful.dto.ApiVo;

public abstract class JsonStreamApiComponentBase extends ApiHelpBase implements JsonStreamApiComponent, MyJsonStreamApiComponent {
	private static Logger logger = LoggerFactory.getLogger(JsonStreamApiComponentBase.class);

	@Autowired
	private ApiMapper apiMapper;
	@Autowired
	private ApiAuditLogger apiAuditLogger;
	
	@Override
	public final Object doService(ApiVo interfaceVo, JSONObject paramObj, JSONReader jsonReader) throws Exception {
		String error = "";
		Object result = null;
		long startTime = System.currentTimeMillis();
		Boolean status = false;
		// audit.setParam(jsonObj.toString(4));
		try {
			try {
				Object proxy = AopContext.currentProxy();
				Method method = proxy.getClass().getMethod("myDoService", JSONObject.class, JSONReader.class);
				result = method.invoke(proxy, paramObj, jsonReader);
			} catch (IllegalStateException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException ex) {
				result = myDoService(paramObj, jsonReader);
			} catch (Exception ex) {
				throw ex;
			}
			status = true;
		} catch (Exception e) {
			error = e.getMessage() == null ? ExceptionUtils.getStackTrace(e) : e.getMessage();
			throw e;
		} finally {
			if (interfaceVo.getNeedAudit() != null && interfaceVo.getNeedAudit().equals(1)) {
				long endTime = System.currentTimeMillis();
				ApiAuditVo audit = new ApiAuditVo();				
				audit.setToken(interfaceVo.getToken());
				audit.setStatus(status ? ApiAuditVo.SUCCEED : ApiAuditVo.FAILED);
				audit.setTimeCost(endTime - startTime);			
				audit.setServerId(Config.SCHEDULE_SERVER_ID);
				audit.setStartTime(new Date(startTime));
				audit.setEndTime(new Date(endTime));
				UserContext userContext = UserContext.get();
				audit.setUserId(userContext.getUserId());
				HttpServletRequest request = UserContext.get().getRequest();
				String requestIp = IpUtil.getIpAddr(request);				
				audit.setIp(requestIp);
				audit.setAuthType(interfaceVo.getAuthtype()); 
				TenantContext.get().setUseDefaultDatasource(false);
				apiMapper.insertApiAudit(audit);
				apiAuditLogger.log(audit.getUuid(),paramObj, error, result);
			}
		}
		return result;
	}

	public final String getId() {
		return this.getClass().getName();
	}

	@Override
	public final JSONObject help() {
		return getApiComponentHelp(JSONObject.class, JSONReader.class);
	}

}
