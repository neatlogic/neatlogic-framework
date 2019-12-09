package codedriver.framework.restful.core;

import java.lang.reflect.Method;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.apiparam.core.ApiParamFactory;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.auth.core.AuthActionChecker;
import codedriver.framework.common.AuthAction;
import codedriver.framework.common.config.Config;
import codedriver.framework.common.util.IpUtil;
import codedriver.framework.exception.core.ApiRuntimeException;
import codedriver.framework.exception.core.FrameworkExceptionMessageBase;
import codedriver.framework.exception.type.ParamIrregularExceptionMessage;
import codedriver.framework.exception.type.ParamNotExistsExceptionMessage;
import codedriver.framework.exception.type.PermissionDeniedExceptionMessage;
import codedriver.framework.restful.annotation.Input;
import codedriver.framework.restful.annotation.Param;
import codedriver.framework.restful.dao.mapper.ApiMapper;
import codedriver.framework.restful.dto.ApiAuditVo;
import codedriver.framework.restful.dto.ApiVo;

public abstract class ApiComponentBase extends ApiHelpBase implements ApiComponent, MyApiComponent {
	private static final Logger logger = LoggerFactory.getLogger(ApiComponentBase.class.getName());

	@Autowired
	private ApiMapper apiMapper;

	@Autowired
	private ApiAuditLogger apiAuditLogger;
	
	public final Object doService(ApiVo interfaceVo, JSONObject jsonObj) throws Exception {
		String error = "";
		Object result = null;
		boolean status = false;
		long startTime = System.currentTimeMillis();
		try {
			try {
				Object proxy = AopContext.currentProxy();
				Class<?> targetClass = AopUtils.getTargetClass(proxy);
				validApi(targetClass, jsonObj);
				Method method = proxy.getClass().getMethod("myDoService", JSONObject.class);
				result = method.invoke(proxy, jsonObj);
			} catch (IllegalStateException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException ex) {
				validApi(this.getClass(), jsonObj);
				result = myDoService(jsonObj);
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
				apiAuditLogger.log(audit.getUuid(),jsonObj, error, result);
			}
		}
		return result;
	}

	private void validApi(Class<?> apiClass, JSONObject paramObj) throws NoSuchMethodException, SecurityException {
		// 获取目标类
		Boolean isAuth = false;
		if (apiClass != null) {
			AuthAction action = apiClass.getAnnotation(AuthAction.class);
			if (null != action && StringUtils.isNotBlank(action.name())) {
				String actionName = action.name();
				// 判断用户角色是否拥有接口权限
				if (AuthActionChecker.check(actionName)) {
					isAuth = true;
				}
			} else {
				isAuth = true;
			}

			if (!isAuth) {
				throw new ApiRuntimeException(new FrameworkExceptionMessageBase(new PermissionDeniedExceptionMessage()));
			}
			// 判断参数是否合法
			Method method = apiClass.getMethod("myDoService", JSONObject.class);
			if (method != null) {
				Input input = method.getAnnotation(Input.class);
				if (input != null) {
					Param[] params = input.value();
					if (params != null && params.length > 0) {
						for (Param p : params) {
							// 判断是否必填
							if (p.isRequired() && !paramObj.containsKey(p.name())) {
								throw new ApiRuntimeException(new FrameworkExceptionMessageBase(new ParamNotExistsExceptionMessage(p.name())));
							}
							// 参数类型校验
							Object paramValue = paramObj.get(p.name());
							if (paramValue != null && !ApiParamFactory.getAuthInstance(p.type()).validate(paramValue, p.rule())) {
								throw new ApiRuntimeException(new FrameworkExceptionMessageBase(new ParamIrregularExceptionMessage(p.name(), p.type())));
							}
						}
					}
				}
			}
		}
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
