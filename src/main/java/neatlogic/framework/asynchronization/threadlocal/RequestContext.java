/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.asynchronization.threadlocal;

import neatlogic.framework.dto.healthcheck.SqlAuditVo;
import neatlogic.framework.restful.constvalue.RejectSource;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.*;

/**
 * 保存请求信息
 */
public class RequestContext implements Serializable {
    private static final ThreadLocal<RequestContext> instance = new ThreadLocal<>();
    private static final long serialVersionUID = -5420998728515359626L;
    private String url;
    private HttpServletRequest request;
    private HttpServletResponse response;
    //接口访问拒绝来源，租户或接口
    private RejectSource rejectSource;
    //接口访问速率
    private Double apiRate;
    //租户接口访问总速率
    private Double tenantRate;
    //语言
    Locale locale;
    //收集该请求执行的sql语句
    private List<SqlAuditVo> sqlAuditList = Collections.synchronizedList(new ArrayList<>());

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public Double getApiRate() {
        return apiRate;
    }

    public void setApiRate(Double apiRate) {
        this.apiRate = apiRate;
    }

    public RejectSource getRejectSource() {
        return rejectSource;
    }

    public void setRejectSource(RejectSource rejectSource) {
        this.rejectSource = rejectSource;
    }

    public Double getTenantRate() {
        return tenantRate;
    }

    public void setTenantRate(Double tenantRate) {
        this.tenantRate = tenantRate;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public List<SqlAuditVo> getSqlAuditList() {
        return sqlAuditList;
    }

    public void setSqlAuditList(List<SqlAuditVo> sqlAuditList) {
        this.sqlAuditList = sqlAuditList;
    }

    public void addSqlAudit(SqlAuditVo sqlAuditVo) {
        sqlAuditList.add(sqlAuditVo);
    }

    public static RequestContext init(RequestContext _requestContext) {
        RequestContext context = new RequestContext();
        if (_requestContext != null) {
            context.setUrl(_requestContext.getUrl());
            context.setLocale(_requestContext.getLocale());
            context.setSqlAuditList(_requestContext.getSqlAuditList());
        }
        instance.set(context);
        return context;
    }

    public static RequestContext init(HttpServletRequest request, String url, HttpServletResponse response) {
        RequestContext context = new RequestContext(request, url);
        context.setResponse(response);
        instance.set(context);
        if (request.getCookies() != null && request.getCookies().length > 0) {
            Optional<Cookie> languageCookie = Arrays.stream(request.getCookies()).filter(o -> Objects.equals(o.getName(), "neatlogic_language")).findFirst();
            if (languageCookie.isPresent()) {
                context.setLocale(new Locale(languageCookie.get().getValue()));
            } else {
                context.setLocale(Locale.getDefault());
            }
        }
        return context;
    }

    private RequestContext() {

    }

    private RequestContext(HttpServletRequest request, String url) {
        this.url = url;
        this.request = request;
    }

    public static RequestContext get() {
        return instance.get();
    }

    public void release() {
        instance.remove();
    }

}
