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

import static neatlogic.framework.filter.request.RequestBodyTestSupport.isBodyCached;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

/** 在真实 Tomcat 中对照旧读取顺序，避免 Mock 请求自动参数缓存掩盖容器差异。 */
public class RequestBodyContainerTest {
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    /** URL 编码表单保留旧 body 与 query 视图，不隐式新增表单字段合并行为。 */
    @Test public void encodedFormKeepsLegacyContainerSemantics() throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(folder.newFolder("tomcat").getAbsolutePath());
        tomcat.setPort(0);
        tomcat.getConnector().setProperty("address", "127.0.0.1");
        Context context = tomcat.addContext("", folder.newFolder("web").getAbsolutePath());
        Tomcat.addServlet(context, "probe", new HttpServlet() {
            /** 两条分支使用相同原始报文，分别执行旧包装与新资源管理。 */
            @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
                request.setCharacterEncoding("UTF-8");
                response.setCharacterEncoding("UTF-8");
                if (request.getRequestURI().equals("/old")) {
                    byte[] bytes = request.getInputStream().readAllBytes();
                    HttpServletRequest legacy = new HttpServletRequestWrapper(request) {
                        @Override public ServletInputStream getInputStream() {
                            return new SynchronousServletInputStream() {
                                @Override protected InputStream open() { return new ByteArrayInputStream(bytes); }
                            };
                        }
                    };
                    response.getWriter().write(view(legacy));
                } else {
                    try (RequestBodyResources resources = new RequestBodyResources(request, new RequestMultipartResolver())) {
                        resources.forAuthentication(false);
                        assertTrue(isBodyCached(resources));
                        response.getWriter().write(view(resources.forDispatch()));
                    }
                }
            }
        });
        context.addServletMappingDecoded("/*", "probe");
        try {
            tomcat.start();
            String base = "http://127.0.0.1:" + tomcat.getConnector().getLocalPort();
            HttpClient client = HttpClient.newHttpClient();
            String body = "value=body&value=second&text=%E4%B8%AD%E6%96%87";
            String old = call(client, base + "/old?value=query", body);
            String current = call(client, base + "/new?value=query", body);
            assertEquals("query|null|" + body, old);
            assertEquals(old, current);
        } finally {
            tomcat.stop();
            tomcat.destroy();
        }
    }

    /** 参数读取安排在 body 重读前，与现有路径过滤器和 Dispatcher 顺序一致。 */
    private static String view(HttpServletRequest request) throws IOException {
        return request.getParameter("value") + "|" + request.getParameter("text") + "|"
                + new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    /** 使用真实 HTTP 请求触发容器解析，不依赖 Mock 的参数预填充。 */
    private static String call(HttpClient client, String uri, String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(uri)).header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(body)).build();
        HttpResponse<String> result = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, result.statusCode());
        return result.body();
    }
}
