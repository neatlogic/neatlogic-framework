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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.junit.Assert.*;

/** 逐组合执行真实认证、Spring 绑定和六种分发分支，分别观察内存重放、磁盘重放和直接读取。 */
@RunWith(Parameterized.class)
public class RequestBodyRouteMatrixTest extends RequestBodyMvcFixture {
    @Parameterized.Parameter public String authentication;
    @Parameterized.Parameter(1) public String route;
    @Parameterized.Parameter(2) public int length;

    /** 每个组合单独报告，避免一个循环通过掩盖其他场景未执行。 */
    @Parameterized.Parameters(name = "{0}-{1}-{2}bytes")
    public static Collection<Object[]> scenarios() {
        Collection<Object[]> result = new ArrayList<>();
        for (String auth : new String[]{"JWT_COOKIE", "JWT_HEADER", "HMAC", "BEARER", "BASIC"}) {
            for (String route : new String[]{"rest", "fetch", "sse", "binary", "raw", "stream"}) {
                // Basic 的生产入口仅支持 rest、binary 和 stream，不把不支持的通道列为成功场景。
                if (auth.equals("BASIC") && !(route.equals("rest") || route.equals("binary") || route.equals("stream"))) { continue; }
                for (int length : new int[]{8, 200}) { result.add(new Object[]{auth, route, length}); }
            }
        }
        return result;
    }

    /** 下游断言完整内容、query 合并、缓存类型以及 finally 清理；不使用真实外部存储。 */
    @Test public void preservesBodyAndReleasesOnlyRequiredCache() throws Exception {
        String payload = "x".repeat(length);
        String body = route.equals("raw") ? payload : "{\"value\":\"" + payload + "\"}";
        omitJwtCookie = !authentication.equals("JWT_COOKIE");
        MockHttpServletRequestBuilder request = builder(route).contentType(route.equals("raw") ? "text/plain" : "application/json")
                .content(body).queryParam("query", "one", "two");
        if (authentication.equals("HMAC")) {
            MockHttpServletRequest sign = new MockHttpServletRequest("POST", "/api/" + route + "/" + TOKEN);
            sign.setQueryString("query=one&query=two");
            request.header("AuthType", "hmac").header("x-access-key", "tester").header("Authorization", signature(sign, body));
        } else if (authentication.equals("JWT_HEADER")) {
            request.header("Authorization", jwtHeader);
        } else if (authentication.equals("BASIC")) {
            request.header("AuthType", "basic").header("Authorization", "Basic " + Base64.getEncoder().encodeToString("tester:test-password".getBytes(StandardCharsets.UTF_8)));
        } else if (authentication.equals("BEARER")) {
            request.header("AuthType", "bearertoken").header("Authorization", "Bearer opaque-test-token");
        }
        MvcResult result = mvc.perform(request).andReturn();
        assertEquals(result.getResponse().getContentAsString(), 200, result.getResponse().getStatus());
        assertEquals(1, calls);
        assertEquals(authentication.equals("HMAC"), cached);
        assertEquals(authentication.equals("HMAC") && body.length() > 64 ? 1 : 0, temporaryFiles);
        if (route.equals("raw")) { assertEquals(payload, received.getString("payload")); }
        else if (route.equals("stream")) { assertEquals(payload, received.getJSONObject("stream").getString("value")); }
        else { assertEquals(payload, received.getString("value")); }
        if (!route.equals("raw")) { assertEquals(2, received.getJSONArray("query").size()); }
        assertEquals(0, directory.getRoot().list().length);
    }
}
