/*
 * Copyright (C) 2025  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.filter;


import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PathTraversalFilter implements Filter {

    // 需要检查的请求参数类型
    private static final String[] CHECKED_PARAMS = {
           "fileName", "filePath", "path", "download"
    };

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        // 检测所有参数值
        for (String paramName : CHECKED_PARAMS) {
            String paramValue = httpRequest.getParameter(paramName);
            if (paramValue != null && isUnsafePath(paramValue)) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid path detected");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isUnsafePath(String value) {
        // 检测多种路径遍历模式（包含URL编码形式）
        return value.contains("../")
                || value.contains("..\\")
                || value.contains("%2e%2e/")
                || value.contains("%2e%2e%2f")
                || value.contains("..%2f")
                || value.matches(".*\\b(?:absolute|true)path\\b.*");
    }
}
