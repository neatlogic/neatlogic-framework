package codedriver.framework.auth.core;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.support.AopUtils;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.apiparam.core.ApiParamFactory;
import codedriver.framework.common.AuthAction;
import codedriver.framework.exception.core.ApiRuntimeException;
import codedriver.framework.exception.core.FrameworkExceptionMessageBase;
import codedriver.framework.exception.type.PermissionDeniedExceptionMessage;
import codedriver.framework.exception.type.ParamIrregularExceptionMessage;
import codedriver.framework.exception.type.ParamNotExistsExceptionMessage;
import codedriver.framework.restful.annotation.Input;
import codedriver.framework.restful.annotation.Param;

public class AuthInterceptor implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		// 获取目标类
		Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
		Boolean isAuth = false;
		if (null != targetClass) {
			AuthAction action = targetClass.getAnnotation(AuthAction.class);
			if (null != action && StringUtils.isNotBlank(action.name())) {
				String actionName = action.name();
				// 判断用户角色是否拥有接口权限
				if (AuthActionChecker.check(actionName)) {
					isAuth = true;
				}
			} else {
				isAuth = true;
			}
		} else {
			isAuth = true;
		}

		if (!isAuth) {
			throw new ApiRuntimeException(new PermissionDeniedExceptionMessage());
		}
		// 判断参数是否合法
		Input input = invocation.getMethod().getAnnotation(Input.class);
		Param[] params = input.value();
		if (params != null && params.length > 0) {
			for (Param p : params) {
				Object[] args = invocation.getArguments();
				for (Object arg : args) {
					if (arg instanceof JSONObject) {
						JSONObject paramJson = (JSONObject) arg;
						// 判断是否必填
						if (p.isRequired() && !paramJson.containsKey(p.name())) {
							throw new ApiRuntimeException(new FrameworkExceptionMessageBase(new ParamNotExistsExceptionMessage(p.name())));
						}
						// 参数类型校验
						Object paramValue = paramJson.get(p.name());
						if (paramValue != null && !ApiParamFactory.getAuthInstance(p.type()).validate(paramValue)) {
							throw new ApiRuntimeException(new FrameworkExceptionMessageBase(new ParamIrregularExceptionMessage(p.name(), p.type())));
						}
					}
				}
			}
		}

		// 执行具体业务逻辑
		return invocation.proceed();
	}

	public static void main(String[] arg) {
		try {
			throw new ApiRuntimeException(new FrameworkExceptionMessageBase(new ParamNotExistsExceptionMessage("")));
		} catch (ApiRuntimeException ex) {
			System.out.println(ex.getErrorCode());
			System.out.println(ex.getMessage());
		}
	}
}
