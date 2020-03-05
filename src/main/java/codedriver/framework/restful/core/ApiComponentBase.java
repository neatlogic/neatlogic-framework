package codedriver.framework.restful.core;

import java.lang.reflect.Method;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.exception.core.ApiRuntimeException;
import codedriver.framework.restful.dao.mapper.ApiMapper;
import codedriver.framework.restful.dto.ApiVo;

public abstract class ApiComponentBase extends ApiValidateAndHelpBase implements IApiComponent, MyApiComponent {

	@Autowired
	private ApiMapper apiMapper;

	public boolean isPrivate() {
		return true;
	}

	public int needAudit() {
		return 0;
	}

	public final Object doService(ApiVo apiVo, JSONObject paramObj) throws Exception {
		String error = "";
		Object result = null;
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
				if (ex.getCause() != null && ex.getCause() instanceof ApiRuntimeException) {
					throw new ApiRuntimeException(ex.getCause().getMessage());
				} else {
					throw ex;
				}
			}
		} catch (Exception e) {
			error = e.getMessage() == null ? ExceptionUtils.getStackTrace(e) : e.getMessage();
			throw e;
		} finally {
			long endTime = System.currentTimeMillis();
			ApiVo apiConfigVo = apiMapper.getApiByToken(apiVo.getToken());
			// 如果没有配置，则使用默认配置
			if (apiConfigVo == null) {
				apiConfigVo = apiVo;
			}
			if (apiConfigVo.getNeedAudit() != null && apiConfigVo.getNeedAudit().equals(1)) {
				saveAudit(apiVo, paramObj, result, error, startTime, endTime);
			}

		}
		return result;
	}

	@Override
	public final JSONObject help() {
		return getApiComponentHelp(JSONObject.class);
	}

}
