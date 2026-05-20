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

package neatlogic.framework.asynchronization.threadlocal;

import neatlogic.framework.common.util.IpUtil;
import neatlogic.framework.dto.healthcheck.RequestSqlAuditVo;
import neatlogic.framework.dto.healthcheck.SqlAuditVo;
import neatlogic.framework.healthcheck.SqlAuditManager;
import neatlogic.framework.restful.constvalue.RejectSource;
import neatlogic.framework.util.SnowflakeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

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
    private String remoteAddr;
    private String param;
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

    private RequestSqlAuditVo requestSqlAuditVo;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
        MDC.put("param", this.param);
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

    public void addSqlAudit(SqlAuditVo sqlAuditVo) {
        if (requestSqlAuditVo == null) {
            // URL监控聚合对象保存在RequestContext，确保一次HTTP请求只生成一条请求级审计记录
            requestSqlAuditVo = new RequestSqlAuditVo(SnowflakeUtil.uniqueLong(), this.url, Thread.currentThread().getName());
        }
        requestSqlAuditVo.addSqlAudit(sqlAuditVo);
    }

    public RequestSqlAuditVo getRequestSqlAuditVo() {
//        if (requestSqlAuditVo != null) {
//            List<RequestSqlAuditVo.SameIdSqlAuditVo> sameIdSqlAuditList = requestSqlAuditVo.getSameIdSqlAuditList();
//            sameIdSqlAuditList.sort((o1, o2) -> Long.compare(o2.getTotalTimeCost(), o1.getTotalTimeCost()));
//        }
        return requestSqlAuditVo;
    }

    public void setRequestSqlAuditVo(RequestSqlAuditVo requestSqlAuditVo) {
        this.requestSqlAuditVo = requestSqlAuditVo;
    }

    public static RequestContext init(RequestContext _requestContext) {
        RequestContext context = new RequestContext();
        if (_requestContext != null) {
            context.setUrl(_requestContext.getUrl());
            context.setLocale(_requestContext.getLocale());
            context.setRequestSqlAuditVo(_requestContext.getRequestSqlAuditVo());
            context.setRemoteAddr(_requestContext.getRemoteAddr());
            context.setParam(_requestContext.getParam());
            String tempUrl = _requestContext.getUrl();
            if (tempUrl == null) {
                tempUrl = StringUtils.EMPTY;
            }
            String remoteAddr = _requestContext.getRemoteAddr();
            if (StringUtils.isNotBlank(remoteAddr)) {
                tempUrl += "(" + remoteAddr + ")";
            }
            MDC.put("url", tempUrl);
            String param = _requestContext.getParam();
            if (StringUtils.isNotBlank(param)) {
                MDC.put("param", param);
            }
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
        String tempUrl = url;
        String remoteAddr = IpUtil.getIpAddr(request);
        if (StringUtils.isNotBlank(remoteAddr)) {
            context.setRemoteAddr(remoteAddr);
            tempUrl += "(" + remoteAddr + ")";
        }
        MDC.put("url", tempUrl);
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
        // 请求结束时从RequestContext读取URL监控聚合对象，避免拦截器额外维护ThreadLocal状态
        if (this.requestSqlAuditVo != null) {
            SqlAuditManager.addRequestSqlAudit(this.requestSqlAuditVo);
        }
        MDC.clear();
        instance.remove();
    }

}
