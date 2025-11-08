/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
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
