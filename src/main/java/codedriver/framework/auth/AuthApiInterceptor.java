package codedriver.framework.auth;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.support.AopUtils;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.auth.param.AuthParamFactory;
import codedriver.framework.common.AuthAction;
import codedriver.framework.exception.ApiRuntimeException;
import codedriver.framework.exception.AuthActionExceptionMessage;
import codedriver.framework.exception.AuthParamsExceptionMessage;
import codedriver.framework.restful.annotation.Input;
import codedriver.framework.restful.annotation.Param;

public class AuthApiInterceptor implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		// 获取目标类
		Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
		Boolean isAuth = false;
		if (null != targetClass) {
			AuthAction action = targetClass.getAnnotation(AuthAction.class);
			if (null != action && !StringUtils.isBlank(action.name())) {
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
			throw new ApiRuntimeException(new AuthActionExceptionMessage(""));
		}
		//判断参数是否合法
		Input input = invocation.getMethod().getAnnotation(Input.class);
		Param[] params = input.value();
		if (params != null && params.length > 0) {
			for (Param p : params) {
				Object[] args = invocation.getArguments();
				for(Object arg : args) {
					if(arg instanceof JSONObject) {
						JSONObject paramJson = (JSONObject) arg;
						//判断是否必填
						if(p.isRequired()&&!paramJson.containsKey(p.name())) {
							throw new ApiRuntimeException(new AuthParamsExceptionMessage(p.name() ));
						}
						//参数类型校验
						String param = paramJson.getString(p.name());
						if(param!=null && !AuthParamFactory.getAuthInstance(p.type()).doAuth(param)) {
							throw new ApiRuntimeException(new AuthParamsExceptionMessage(p.name(),p.type()));
						}
					}
				}
			}
		}
		
		// 执行具体业务逻辑
		return invocation.proceed();
	}

}
