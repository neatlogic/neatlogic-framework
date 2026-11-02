/*
 * Copyright (C) 2026  深圳极向量科技有限公司 All Rights Reserved.
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

package neatlogic.framework.filter.request;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.exception.request.RequestBodyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/** 只处理受管理请求的读取错误；其他异常仍由现有认证、Spring 和业务协议处理。 */
public class RequestBodyErrorHandler implements HandlerExceptionResolver, Ordered {
    private static final Logger logger = LoggerFactory.getLogger(RequestBodyErrorHandler.class);

    /** 在 Spring 默认错误映射前识别被包装的资源错误。 */
    @Override public int getOrder() { return Ordered.HIGHEST_PRECEDENCE; }

    /** 参数绑定可能把超限包装为 HttpMessageNotReadableException，此处保持原始状态码。 */
    @Override public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
                                                  Object handler, Exception ex) {
        RequestBodyException error = find(request, ex);
        if (error == null) { return null; }
        logger.error("Request body rejected: {}", request.getRequestURI(), ex);
        try { write(response, error); }
        catch (IOException failure) { logger.error("Cannot write request body error", failure); }
        return new ModelAndView();
    }

    /** 优先采用资源层记录的错误，再检查包装链，并防止异常 cause 环。 */
    public static RequestBodyException find(HttpServletRequest request, Throwable ex) {
        RequestBodyResources resources = (RequestBodyResources) request.getAttribute(RequestBodyResources.ATTRIBUTE);
        if (resources == null) { return null; }
        if (resources.getFailure() != null) { return resources.getFailure(); }
        Set<Throwable> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        for (Throwable current = ex; current != null && seen.add(current); current = current.getCause()) {
            if (current instanceof RequestBodyException) { return (RequestBodyException) current; }
        }
        return null;
    }

    /** 不修改认证 Cookie；已经提交的响应不可重写，记录后交由容器结束连接。 */
    public static void write(HttpServletResponse response, RequestBodyException error) throws IOException {
        if (response.isCommitted()) {
            logger.error("Response already committed before request body failure", error);
            return;
        }
        response.resetBuffer();
        response.setStatus(error.getStatus());
        response.setContentType(Config.RESPONSE_TYPE_JSON);
        JSONObject result = new JSONObject();
        result.put("Status", "ERROR");
        result.put("Message", error.getMessage());
        response.getWriter().write(result.toJSONString());
    }
}
