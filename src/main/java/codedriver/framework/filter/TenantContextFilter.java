package codedriver.framework.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import codedriver.framework.threadlocal.TenantContext;

public class TenantContextFilter extends OncePerRequestFilter {
	// private ServletContext context;

	/**
	 * Default constructor.
	 */
	public TenantContextFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	@Override
	public void destroy() {
		TenantContext.get().release();// 清除线程变量值
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String uuid = request.getParameter("uuid");
		TenantContext context = null;
		if (StringUtils.isNotBlank(uuid)) {
			context = TenantContext.init(uuid);// 初始化线程变量值
		} else {
			context = TenantContext.init("master");// 初始化线程变量值
		}
		try {
			filterChain.doFilter(request, response);
		} finally {
			if (context != null) {
				context.release();
			}
		}

	}
}
