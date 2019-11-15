package codedriver.framework.auth;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;

import com.alibaba.druid.util.StringUtils;

import codedriver.framework.common.AuthAction;
import codedriver.framework.common.ReturnJson;
import codedriver.framework.exception.ApiNotFoundExceptionMessage;
import codedriver.framework.exception.ApiRuntimeException;



public class AuthApiInterceptor implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		//获取目标类
        Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
        Boolean isAuth = false;
        if(null != targetClass) {
        	AuthAction action = targetClass.getAnnotation(AuthAction.class);
        	
        	if(null != action && !StringUtils.isEmpty(action.name())) {
        		String actionName = action.name();
        		//判断用户角色是否拥有接口权限
        		if (AuthActionChecker.check(actionName)) {
        			isAuth = true;
    			} 
        	}
        }
        if(!isAuth) {
			throw new ApiRuntimeException(new ApiNotFoundExceptionMessage("123"));
		}
        //执行具体业务逻辑
        return invocation.proceed();
	}

}
