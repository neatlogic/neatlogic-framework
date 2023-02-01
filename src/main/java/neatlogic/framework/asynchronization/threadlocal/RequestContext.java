/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.asynchronization.threadlocal;

import neatlogic.framework.restful.constvalue.RejectSource;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * 保存请求信息
 */
public class RequestContext implements Serializable {
    private static final ThreadLocal<RequestContext> instance = new ThreadLocal<>();
    private String url;
    private HttpServletRequest request;
    //接口访问拒绝来源，租户或接口
    private RejectSource rejectSource;
    //接口访问速率
    private Double apiRate;
    //租户接口访问总速率
    private Double tenantRate;
    //接口是否豁免license
    private Boolean isExemptLicense = false;

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

    public Boolean getIsExemptLicense() {
        return isExemptLicense;
    }

    public void setIsExemptLicense(Boolean isExemptLicense) {
        this.isExemptLicense = isExemptLicense;
    }

    public static RequestContext init(RequestContext _requestContext) {
        RequestContext context = new RequestContext();
        if (_requestContext != null) {
            context.setUrl(_requestContext.getUrl());
        }
        instance.set(context);
        return context;
    }

    public static RequestContext init(HttpServletRequest request, String url) {
        RequestContext context = new RequestContext(request, url);
        instance.set(context);
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
