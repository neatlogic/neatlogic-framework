package codedriver.framework.auth;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;

import codedriver.framework.common.AuthAction;
import codedriver.framework.exception.ApiNotFoundExceptionMessage;
import codedriver.framework.exception.ApiRuntimeException;



public class TestInterceptor implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		//获取目标类
        Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
        System.out.println(targetClass.getAnnotation(AuthAction.class).name());
        //判断权限
        throw new ApiRuntimeException(new ApiNotFoundExceptionMessage("123"));
        //执行具体业务逻辑
        //return invocation.proceed();
	}

}
