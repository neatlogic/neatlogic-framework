package codedriver.framework.restful.core;

import java.lang.reflect.Method;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.common.config.Config;
import codedriver.framework.common.util.IpUtil;
import codedriver.framework.restful.dao.mapper.ApiMapper;
import codedriver.framework.restful.dto.ApiAuditVo;
import codedriver.framework.restful.dto.ApiVo;
import codedriver.framework.restful.logger.ApiAuditContent;
import codedriver.framework.restful.logger.ApiAuditLogger;

public abstract class ApiComponentBase extends ApiValidateAndHelpBase implements ApiComponent, MyApiComponent {
	private static final Logger logger = LoggerFactory.getLogger(ApiComponentBase.class.getName());

	@Autowired
	private ApiMapper apiMapper;

	public boolean isPrivate() {
		return true;
	}

	public final Object doService(ApiVo interfaceVo, JSONObject paramObj) throws Exception {
		String error = "";
		Object result = null;
		boolean status = false;
		long startTime = System.currentTimeMillis();
		try {
			try {
				Object proxy = AopContext.currentProxy();
				Class<?> targetClass = AopUtils.getTargetClass(proxy);
				validApi(targetClass, paramObj, JSONObject.class);
				Method method = proxy.getClass().getMethod("myDoService", JSONObject.class);
				result = method.invoke(proxy, paramObj);
			} catch (IllegalStateException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException ex) {
				validApi(this.getClass(), paramObj, JSONObject.class);
				result = myDoService(paramObj);
			} catch (Exception ex) {
				throw ex;
			}
			status = true;
		} catch (Exception e) {
			error = e.getMessage() == null ? ExceptionUtils.getStackTrace(e) : e.getMessage();
			throw e;
		} finally {
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
			Integer needAudit = interfaceVo.getNeedAudit();
			if (needAudit == null) {
				ApiVo apiVo = apiMapper.getApiByToken(interfaceVo.getToken());
				if (apiVo != null) {
					needAudit = apiVo.getNeedAudit();
				}
			}
			if (needAudit != null && needAudit.intValue() == 1) {
				String tenentUuid = TenantContext.get().getTenantUuid();
				int index = Math.abs(tenentUuid.hashCode()) % ApiAuditLogger.THREAD_COUNT;
				ApiAuditLogger.getQueue(index).offer(new ApiAuditContent(tenentUuid, audit.getUuid(), paramObj, error, result));
			}
		}
		return result;
	}

	@Override
	public final String getId() {
		return ClassUtils.getUserClass(this.getClass()).getName();
	}

	@Override
	public final JSONObject help() {
		return getApiComponentHelp(JSONObject.class);
	}

}
