package codedriver.framework.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import codedriver.framework.common.AuthAction;
import codedriver.framework.common.ReturnJson;

public class AuthInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
			AuthAction roleCheck = ((HandlerMethod) handler).getMethod().getDeclaringClass().getAnnotation(AuthAction.class);
			if (roleCheck != null) {
				String action = roleCheck.name();
				if (action == null || action.trim().equals("")) {
					return true;
				}
				if (AuthActionChecker.check(action)) {
					return true;
				} else {
					if (request.getHeader("X-Requested-With") != null) {
						if (request.getHeader("X-Requested-With").equals("XMLHttpRequest")) {
							response.setStatus(403);
							//TODO 
							//Translator.translate("您没有权限进行当前操作")
							ReturnJson.error("您没有权限进行当前操作", response);
						}
					} else {
						request.getRequestDispatcher("/WEB-INF/views/commons/403.jsp").forward(request, response);
					}
					return false;
				}
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		// TODO Auto-generated method stub

	}
}
