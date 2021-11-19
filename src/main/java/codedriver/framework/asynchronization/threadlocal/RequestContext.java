/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.asynchronization.threadlocal;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * 保存请求信息
 */
public class RequestContext implements Serializable {
    private static final ThreadLocal<RequestContext> instance = new ThreadLocal<>();
    private String url;
    private HttpServletRequest request;

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
