package codedriver.framework.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.dao.mapper.ModuleMapper;

public class TenantModuleFilter extends OncePerRequestFilter {

	@Autowired
	ModuleMapper moduleMapper;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		TenantContext tenantContext = TenantContext.get();
		String tenant = tenantContext.getTenantUuid();
		if (StringUtils.isNotBlank(tenant)) {
			// 使用master库
			tenantContext.setUseDefaultDatasource(true);
			TenantContext.get().setActiveModuleList(moduleMapper.getActiveModuleListByTenantUuid(tenant));
			tenantContext.setTenantUuid(tenant);
			// 还原回租户库
			tenantContext.setUseDefaultDatasource(false);
		}
		filterChain.doFilter(request, response);
	}

	/**
	 * @see Filter#destroy()
	 */
	@Override
	public void destroy() {
		TenantContext.get().release();// 清除线程变量值
	}
}
