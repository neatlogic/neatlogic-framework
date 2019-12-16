package codedriver.framework.restful.core;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.restful.dao.mapper.ApiMapper;
import codedriver.framework.restful.dto.ApiVo;

public abstract class BinaryStreamApiComponentBase extends ApiValidateAndHelpBase implements BinaryStreamApiComponent, MyBinaryStreamApiComponent {
	// private static Logger logger =
	// LoggerFactory.getLogger(BinaryStreamApiComponentBase.class);

	@Autowired
	private ApiMapper apiMapper;

	public boolean isPrivate() {
		return true;
	}

	public int needAudit() {
		return 0;
	}

	@Override
	public final Object doService(ApiVo apiVo, JSONObject paramObj, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String error = "";
		Object result = null;
		long startTime = System.currentTimeMillis();
		try {
			try {
				Object proxy = AopContext.currentProxy();
				Class<?> targetClass = AopUtils.getTargetClass(proxy);
				validApi(targetClass, paramObj, JSONObject.class, HttpServletRequest.class, HttpServletResponse.class);
				Method method = proxy.getClass().getMethod("myDoService", JSONObject.class, HttpServletRequest.class, HttpServletResponse.class);
				result = method.invoke(proxy, paramObj, request, response);
			} catch (IllegalStateException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException ex) {
				validApi(this.getClass(), paramObj, JSONObject.class, HttpServletRequest.class, HttpServletResponse.class);
				result = myDoService(paramObj, request, response);
			} catch (Exception ex) {
				throw ex;
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

	public final String getId() {
		return ClassUtils.getUserClass(this.getClass()).getName();
	}

	@Override
	public final JSONObject help() {
		return getApiComponentHelp(JSONObject.class, HttpServletRequest.class, HttpServletResponse.class);
	}

}
