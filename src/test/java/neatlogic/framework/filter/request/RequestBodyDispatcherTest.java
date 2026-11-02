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

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import java.util.Set;
import javax.servlet.http.Cookie;
import neatlogic.framework.common.config.Config;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/** 在真实 JWT/HMAC、Spring 绑定和 Dispatcher 上验证请求体读取及资源生命周期。 */
public class RequestBodyDispatcherTest extends RequestBodyMvcFixture {
    /** 普通 JSON 参数只解析一次原始 body，query 仍覆盖 JSON 同名字段。 */
    @Test public void jwtJsonQueryMergeDoesNotCache() throws Exception {
        MvcResult result = mvc.perform(builder("rest").contentType("application/json").content("{\"value\":\"body\"}").param("value", "query")).andReturn();
        assertEquals(result.getResponse().getContentAsString(), 200, result.getResponse().getStatus());
        assertEquals(1, calls); assertFalse(cached); assertEquals("query", received.getString("value"));
    }

    /** fetch、SSE 和 JSON binary 保持 Spring 字符串绑定及参数合并路径。 */
    @Test public void otherJsonBranchesDoNotCache() throws Exception {
        for (String branch : new String[]{"fetch", "sse", "binary"}) {
            calls = 0;
            MvcResult result = mvc.perform(builder(branch).contentType("application/json").content("{\"value\":3}")).andReturn();
            assertEquals(result.getResponse().getContentAsString(), 200, result.getResponse().getStatus());
            assertEquals(1, calls); assertFalse(cached); assertEquals(3, received.getIntValue("value"));
        }
    }

    /** HMAC 普通 JSON 经真实 @RequestBody 绑定后仍完整，且只在认证消费后建缓存。 */
    @Test public void hmacJsonCanReachDispatcher() throws Exception {
        omitJwtCookie = true;
        String body = "{\"value\":\"" + "x".repeat(100) + "\"}";
        MockHttpServletRequest sign = new MockHttpServletRequest("POST", "/api/rest/" + TOKEN);
        MvcResult result = mvc.perform(builder("rest").header("AuthType", "hmac").header("x-access-key", "tester")
                .header("Authorization", RequestBodyResourcesTest.signature(sign, body)).contentType("application/json").content(body)).andReturn();
        assertEquals(result.getResponse().getContentAsString(), 200, result.getResponse().getStatus());
        assertEquals(1, calls); assertTrue(cached); assertEquals(100, received.getString("value").length());
        assertEquals(0, directory.getRoot().list().length);
    }

    /** 上传进入实际 Dispatcher 和文件业务组件，原始 body 不缓存且解析后参数可重复读取。 */
    @Test public void jwtUploadUsesOnlyMultipartStorage() throws Exception {
        MvcResult result = mvc.perform(builder("binary").contentType("multipart/form-data; boundary=probe")
                .content(RequestBodyResourcesTest.multipart(200))).andReturn();
        assertEquals(result.getResponse().getContentAsString(), 200, result.getResponse().getStatus());
        assertEquals(1, calls); assertFalse(cached); assertEquals(3, itemCount);
        assertEquals(0, directory.getRoot().list().length);
    }

    /** HMAC 上传完整穿过认证、路径过滤器和 Dispatcher，既不重复解析也不缓存原始 body。 */
    @Test public void hmacUploadReusesParsedRequest() throws Exception {
        omitJwtCookie = true;
        MockHttpServletRequest sign = new MockHttpServletRequest("POST", "/api/binary/" + TOKEN);
        JSONObject fields = new JSONObject(); fields.put("value", "中文");
        MvcResult result = mvc.perform(builder("binary").contentType("multipart/form-data; boundary=probe")
                .header("AuthType", "hmac").header("x-access-key", "tester")
                .header("Authorization", RequestBodyResourcesTest.signature(sign, fields.toJSONString()))
                .content(RequestBodyResourcesTest.multipart(200))).andReturn();
        assertEquals(result.getResponse().getContentAsString(), 200, result.getResponse().getStatus());
        assertEquals(1, calls); assertFalse(cached); assertEquals(3, itemCount);
        assertEquals(0, directory.getRoot().list().length);
    }

    /** HMAC 缺失头部时不解析上传；签名不匹配时已解析文件也必须删除。 */
    @Test public void hmacFailuresDoNotLeakUploadFiles() throws Exception {
        omitJwtCookie = true;
        for (String authorization : new String[]{"", "Hmac invalid"}) {
            MvcResult result = mvc.perform(builder("binary").contentType("multipart/form-data; boundary=probe")
                    .header("AuthType", "hmac").header("x-access-key", "tester").header("Authorization", authorization)
                    .content(RequestBodyResourcesTest.multipart(200))).andReturn();
            assertTrue(result.getResponse().getStatus() >= 400); assertEquals(0, calls);
            RequestBodyResources resources = (RequestBodyResources) result.getRequest().getAttribute(RequestBodyResources.ATTRIBUTE);
            assertFalse(isBodyCached(resources));
            assertEquals(authorization.isEmpty() ? 0 : 3, resources.items.size());
            assertEquals(0, directory.getRoot().list().length);
        }
    }

    /** 默认 JWT 成功时不因 Header 指定 HMAC 而执行额外认证或建立 body 缓存。 */
    @Test public void defaultAuthenticationStillTakesPriority() throws Exception {
        MvcResult result = mvc.perform(builder("rest").header("AuthType", "hmac").header("Authorization", jwtHeader)
                .contentType("application/json").content("{\"value\":1}")).andReturn();
        assertEquals(200, result.getResponse().getStatus()); assertEquals(1, calls); assertFalse(cached);
    }

    /** JSON stream 直接由 Dispatcher 的 JSONReader 消费，不因下游读流而建立认证缓存。 */
    @Test public void streamReadsOriginalBodyOnce() throws Exception {
        MvcResult result = mvc.perform(builder("stream").contentType("application/json").content("{\"value\":2}")).andReturn();
        assertEquals(result.getResponse().getContentAsString(), 200, result.getResponse().getStatus());
        assertEquals(1, calls); assertFalse(cached); assertEquals(2, received.getJSONObject("stream").getIntValue("value"));
    }

    /** raw 分支保留 payload，不增加原始 body 的第二份存储。 */
    @Test public void rawReadsOriginalBodyOnce() throws Exception {
        MvcResult result = mvc.perform(builder("raw").contentType("text/plain").content("raw-payload")).andReturn();
        assertEquals(result.getResponse().getContentAsString(), 200, result.getResponse().getStatus());
        assertEquals(1, calls); assertFalse(cached); assertEquals("raw-payload", received.getString("payload"));
    }

    /** Spring 对读取异常的包装不得把 413 改为 400 或过滤器的 520。 */
    @Test public void oversizedJsonReturns413WithoutBusinessCall() throws Exception {
        setStatic(Config.class, "REQUEST_BODY_MAX_BYTES", 10L);
        MvcResult result = mvc.perform(builder("rest").contentType("application/json").content("{\"value\":123456}")).andReturn();
        assertEquals(result.getResponse().getContentAsString(), 413, result.getResponse().getStatus());
        assertEquals(0, calls); assertNull(result.getResponse().getHeader("Set-Cookie"));
    }

    /** 路径参数所有值都必须校验，拒绝时删除已解析的上传文件。 */
    @Test public void unsafeSecondPathValueStopsBusinessAndCleansFiles() throws Exception {
        MvcResult result = mvc.perform(builder("binary").contentType("multipart/form-data; boundary=probe")
                .content(RequestBodyResourcesTest.multipart(200)).param("path", "safe", "../secret")).andReturn();
        assertEquals(403, result.getResponse().getStatus()); assertEquals(0, calls);
        assertEquals(0, directory.getRoot().list().length);
    }

    /** 业务处理已取得文件后失败，上传所有者仍必须清理临时资源。 */
    @Test public void businessFailureCleansParsedFiles() throws Exception {
        failBusiness = true;
        MvcResult result = mvc.perform(builder("binary").contentType("multipart/form-data; boundary=probe")
                .content(RequestBodyResourcesTest.multipart(200))).andReturn();
        assertTrue(result.getResponse().getStatus() >= 400); assertEquals(1, calls);
        assertEquals(0, directory.getRoot().list().length);
    }

    /** 无效租户在任何缓存或解析前被拒绝。 */
    @Test public void missingTenantDoesNotCreateResources() throws Exception {
        String path = "/api/binary/" + TOKEN;
        MvcResult result = mvc.perform(post(path).servletPath(path).header("Tenant", "missing-body-test-tenant")
                .contentType("multipart/form-data; boundary=probe").content(RequestBodyResourcesTest.multipart(200))).andReturn();
        assertEquals(521, result.getResponse().getStatus()); assertEquals(0, calls);
        assertNull(result.getRequest().getAttribute(RequestBodyResources.ATTRIBUTE));
        assertEquals(0, directory.getRoot().list().length);
    }

    /** 无 body 的 GET 只读取查询参数，不创建任何重放缓存或临时文件。 */
    @Test public void getWithoutBodyDoesNotCache() throws Exception {
        String path = "/api/rest/" + TOKEN;
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get(path)
                .servletPath(path).header("Tenant", TENANT).cookie(new Cookie("neatlogic_authorization", jwtCookie))
                .queryParam("value", "first", "second")).andReturn();
        assertEquals(result.getResponse().getContentAsString(), 200, result.getResponse().getStatus());
        assertEquals(1, calls); assertFalse(cached); assertEquals(0, temporaryFiles);
        assertEquals(2, received.getJSONArray("value").size());
        assertEquals(0, directory.getRoot().list().length);
    }

    /** 无效 JWT 不应触发上传解析或业务，不能将合法 Cookie 与非法 Header 混淆。 */
    @Test public void invalidHeaderOverridesValidCookieWithoutReadingUpload() throws Exception {
        MvcResult result = mvc.perform(builder("binary").header("Authorization", "Bearer invalid-token")
                .contentType("multipart/form-data; boundary=probe").content(RequestBodyResourcesTest.multipart(200))).andReturn();
        assertTrue(result.getResponse().getStatus() >= 400); assertEquals(0, calls);
        RequestBodyResources resources = (RequestBodyResources) result.getRequest().getAttribute(RequestBodyResources.ATTRIBUTE);
        assertFalse(isBodyCached(resources)); assertTrue(resources.items.isEmpty());
        assertEquals(0, directory.getRoot().list().length);
    }
}
