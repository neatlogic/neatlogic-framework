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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.filter.PathTraversalFilter;
import org.apache.catalina.Context;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.DispatcherServlet;

import static org.junit.Assert.*;

/** 真实 HTTP 容器校验，防止 Mock 请求的预填参数或可重复输入流掩盖读取错误。 */
public class RequestBodyHttpTest extends RequestBodyMvcFixture {
    @Rule public TemporaryFolder serverDirectory = new TemporaryFolder();

    /** 通过网络逐项执行三类认证的六个分发入口、上传以及无 body GET。 */
    @Test public void realHttpRequestsPreserveBodyAndMultipart() throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(serverDirectory.newFolder("tomcat").getAbsolutePath());
        tomcat.setPort(0); tomcat.getConnector().setProperty("address", "127.0.0.1");
        Context context = tomcat.addContext("", serverDirectory.newFolder("web").getAbsolutePath());
        ((StandardContext) context).setWorkDir(directory.getRoot().getAbsolutePath());
        DispatcherServlet spring = new DispatcherServlet(mvc.getDispatcherServlet().getWebApplicationContext());
        Tomcat.addServlet(context, "dispatcher", new HttpServlet() {
            /** 使用标准 Spring Servlet，避免 MockMvc 专用 Servlet 依赖测试结果请求属性。 */
            @Override public void init() throws ServletException {
                spring.init(getServletConfig());
                ReflectionTestUtils.setField(spring, "multipartResolver", new RequestMultipartResolver() {
                    /** 已解析上传必须由 Spring 直接复用，不能再次进入全局解析器。 */
                    @Override public MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) {
                        throw new AssertionError("Spring 重复解析了入口已解析的上传");
                    }
                    /** Spring 不应清理入口拥有的上传，资源生命周期必须覆盖整个过滤器链。 */
                    @Override public void cleanupMultipart(MultipartHttpServletRequest request) {
                        throw new AssertionError("Spring 接管了入口上传的清理");
                    }
                });
            }

            /** 使用真实请求运行过滤器与已初始化 Spring Servlet；释放工作线程上下文。 */
            @Override protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                request.setCharacterEncoding("UTF-8");
                try {
                    filter.doFilter(request, response, (authenticated, target) -> new PathTraversalFilter().doFilter(authenticated, target,
                            (parsed, output) -> spring.service(parsed, output)));
                } finally {
                    if (UserContext.get() != null) { UserContext.get().release(); }
                    if (RequestContext.get() != null) { RequestContext.get().release(); }
                    if (TenantContext.get() != null) { TenantContext.get().release(); }
                }
            }
        });
        context.addServletMappingDecoded("/", "dispatcher");
        try {
            tomcat.start();
            assertEquals(directory.getRoot(), context.getServletContext().getAttribute("javax.servlet.context.tempdir"));
            String base = "http://127.0.0.1:" + tomcat.getConnector().getLocalPort();
            HttpClient client = HttpClient.newHttpClient();
            calls = 0;
            HttpResponse<String> get = client.send(HttpRequest.newBuilder(URI.create(base + "/api/rest/" + TOKEN + "?value=query"))
                    .header("Tenant", TENANT).header("Cookie", "neatlogic_authorization=" + jwtCookie).GET().build(), HttpResponse.BodyHandlers.ofString());
            assertEquals(get.body(), 200, get.statusCode()); assertEquals(1, calls); assertFalse(cached);
            assertEquals("query", received.getString("value")); assertEquals(0, temporaryFiles);
            for (String auth : new String[]{"JWT", "HMAC", "BEARER"}) {
                for (String route : new String[]{"rest", "fetch", "sse", "binary", "raw", "stream", "upload"}) {
                    calls = 0;
                    String path = "/api/" + (route.equals("upload") ? "binary" : route) + "/" + TOKEN;
                    String body = route.equals("raw") ? "中文".repeat(100) : "{\"value\":\"" + "中文".repeat(100) + "\"}";
                    byte[] bytes = route.equals("upload") ? RequestBodyResourcesTest.multipart(1024 * 1024) : body.getBytes(StandardCharsets.UTF_8);
                    HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(base + path + "?query=one&query=two"))
                            .header("Tenant", TENANT).header("Content-Type", route.equals("upload") ? "multipart/form-data; boundary=probe"
                                    : route.equals("raw") ? "text/plain;charset=UTF-8" : "application/json;charset=UTF-8")
                            // 上传使用未知 Content-Length 的分块传输，覆盖真实容器的一次性网络流。
                            .POST(route.equals("upload") ? HttpRequest.BodyPublishers.ofInputStream(() -> new ByteArrayInputStream(bytes))
                                    : HttpRequest.BodyPublishers.ofByteArray(bytes));
                    if (auth.equals("JWT")) { request.header("Cookie", "neatlogic_authorization=" + jwtCookie); }
                    else if (auth.equals("BEARER")) { request.header("AuthType", "bearertoken").header("Authorization", "Bearer opaque-test-token"); }
                    else {
                        MockHttpServletRequest sign = new MockHttpServletRequest("POST", path); sign.setQueryString("query=one&query=two");
                        request.header("AuthType", "hmac").header("x-access-key", "tester").header("Authorization", signature(sign, route.equals("upload") ? "{\"value\":\"中文\"}" : body));
                    }
                    HttpResponse<String> result = client.send(request.build(), HttpResponse.BodyHandlers.ofString());
                    assertEquals(auth + "/" + route + ": " + result.body(), 200, result.statusCode());
                    assertEquals(1, calls); assertEquals(auth.equals("HMAC") && !route.equals("upload"), cached);
                    assertEquals(auth + "/" + route, route.equals("upload") || auth.equals("HMAC") ? 1 : 0, temporaryFiles);
                    if (route.equals("upload")) { assertEquals(1024 * 1024, temporaryBytes); }
                    if (route.equals("raw")) { assertEquals(body, received.getString("payload")); }
                    else if (route.equals("stream")) { assertEquals("中文".repeat(100), received.getJSONObject("stream").getString("value")); }
                    else if (!route.equals("upload")) { assertEquals("中文".repeat(100), received.getString("value")); }
                    if (!route.equals("raw")) { assertEquals(2, received.getJSONArray("query").size()); }
                    assertEquals(0, directory.getRoot().list().length);
                }
            }
        } finally { tomcat.stop(); tomcat.destroy(); }
    }
}
