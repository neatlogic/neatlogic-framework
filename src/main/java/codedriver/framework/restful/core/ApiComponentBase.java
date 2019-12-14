package codedriver.framework.restful.core;

import java.lang.reflect.Method;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.common.config.Config;
import codedriver.framework.common.util.IpUtil;
import codedriver.framework.dao.mapper.ConfigMapper;
import codedriver.framework.dto.ConfigVo;
import codedriver.framework.restful.dao.mapper.ApiMapper;
import codedriver.framework.restful.dto.ApiAuditVo;
import codedriver.framework.restful.dto.ApiVo;

public abstract class ApiComponentBase extends ApiValidateAndHelpBase implements ApiComponent, MyApiComponent {
	// private static final Logger logger =
	// LoggerFactory.getLogger(ApiComponentBase.class.getName());
	private static final String API_AUDIT_CONFIG_KEY = "api_audit_config";

	@Autowired
	private ApiMapper apiMapper;

	@Autowired
	private ConfigMapper configMapper;

	public boolean isPrivate() {
		return true;
	}

	public int needAudit() {
		return 0;
	}

	public final Object doService(ApiVo apiVo, JSONObject paramObj) throws Exception {
		String error = "";
		Object result = null;
		boolean hasError = false;
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
		} catch (Exception e) {
			error = e.getMessage() == null ? ExceptionUtils.getStackTrace(e) : e.getMessage();
			hasError = true;
			throw e;
		} finally {
			long endTime = System.currentTimeMillis();
			ApiVo apiConfigVo = apiMapper.getApiByToken(apiVo.getToken());
			// 如果没有配置，则使用默认配置
			if (apiConfigVo == null) {
				apiConfigVo = apiVo;
			}
			if (apiConfigVo.getNeedAudit() != null && apiConfigVo.getNeedAudit().equals(1)) {
				ConfigVo configVo = configMapper.getConfigByKey(API_AUDIT_CONFIG_KEY);
				ApiAuditVo audit = new ApiAuditVo();
				audit.setToken(apiVo.getToken());
				audit.setTenant(TenantContext.get().getTenantUuid());
				audit.setStatus(hasError ? ApiAuditVo.FAILED : ApiAuditVo.SUCCEED);
				audit.setServerId(Config.SCHEDULE_SERVER_ID);
				audit.setStartTime(new Date(startTime));
				audit.setEndTime(new Date(endTime));
				if (configVo != null && StringUtils.isNotBlank(configVo.getValue())) {
					try {
						JSONObject auditConfig = JSONObject.parseObject(configVo.getValue());
						if (auditConfig.containsKey("savepath")) {
							audit.setLogPath(auditConfig.getString("savepath"));
						}
					} catch (Exception ex) {

					}
				}
				UserContext userContext = UserContext.get();
				audit.setUserId(userContext.getUserId());
				HttpServletRequest request = userContext.getRequest();
				String requestIp = IpUtil.getIpAddr(request);
				audit.setIp(requestIp);
				audit.setAuthType(apiVo.getAuthtype());
				if (paramObj != null) {
					audit.setParam(paramObj.toJSONString());
				}
				if (error != null) {
					audit.setError(error);
				}
				if (result != null) {
					audit.setResult(result);
				}
				saveAudit(audit);
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
