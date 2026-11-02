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
import static neatlogic.framework.filter.request.RequestBodyTestSupport.getBodyCacheFile;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.exception.request.RequestBodyException;
import neatlogic.framework.service.UserService;
import neatlogic.framework.util.SHA256Util;
import neatlogic.module.framework.filter.handler.HmacLoginAuthHandler;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.junit.Rule;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.Arrays;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.*;

/** 使用原始报文验证资源限制，不使用已解析的 MockMultipartHttpServletRequest。 */
public class RequestBodyResourcesTest {
    @Rule public TemporaryFolder folder = new TemporaryFolder();
    private final Map<Field, Object> originalConfig = new LinkedHashMap<>();

    /** 保存请求体配置，避免用例中的小阈值影响其他测试。 */
    @Before public void saveConfig() throws Exception {
        for (Field field : Config.class.getDeclaredFields()) {
            if (field.getName().startsWith("REQUEST_BODY_") || field.getName().startsWith("REQUEST_MULTIPART_")) {
                field.setAccessible(true);
                originalConfig.put(field, field.get(null));
            }
        }
    }

    /** 无论用例成功或失败都恢复原始配置。 */
    @After public void restoreConfig() throws Exception {
        for (Map.Entry<Field, Object> entry : originalConfig.entrySet()) {
            entry.getKey().set(null, entry.getValue());
        }
    }

    /** 生成真实 multipart 字节，覆盖重复字段、中文和超过落盘阈值的文件。 */
    static byte[] multipart(int fileSize) {
        return ("--probe\r\nContent-Disposition: form-data; name=\"value\"\r\n\r\nfirst\r\n"
                + "--probe\r\nContent-Disposition: form-data; name=\"value\"\r\nContent-Type: text/plain; charset=UTF-8\r\n\r\n中文\r\n"
                + "--probe\r\nContent-Disposition: form-data; name=\"upload\"; filename=\"test.bin\"\r\n"
                + "Content-Type: application/octet-stream\r\n\r\n" + "x".repeat(fileSize) + "\r\n--probe--\r\n")
                .getBytes(StandardCharsets.UTF_8);
    }

    /** 临时目录与每个请求隔离，方便检查认证失败等路径是否泄漏资源。 */
    MockHttpServletRequest request(String type, byte[] bytes) {
        MockServletContext context = new MockServletContext();
        context.setAttribute("javax.servlet.context.tempdir", folder.getRoot());
        MockHttpServletRequest request = new MockHttpServletRequest(context, "POST", "/api/binary/probe");
        request.setContentType(type);
        request.setCharacterEncoding("UTF-8");
        request.setContent(bytes);
        return request;
    }

    /** 测试使用小阈值，避免为阈值和失败验证构造大数组。 */
    RequestBodyResources resources(HttpServletRequest request, String... settings) throws Exception {
        Properties values = new Properties();
        values.setProperty("request.body.memory.threshold", "64");
        values.setProperty("request.body.max.bytes", "10485760");
        values.setProperty("request.multipart.max.bytes", "1073741824");
        values.setProperty("request.multipart.memory.threshold", "64");
        values.setProperty("request.multipart.fields.max.bytes", "10485760");
        values.setProperty("request.multipart.parts.max", "128");
        for (int i = 0; i < settings.length; i += 2) { values.setProperty(settings[i], settings[i + 1]); }
        for (String key : values.stringPropertyNames()) {
            Field field = Config.class.getDeclaredField(key.replace('.', '_').toUpperCase(Locale.ROOT));
            field.setAccessible(true);
            if (field.getType() == int.class) { field.setInt(null, Integer.parseInt(values.getProperty(key))); }
            else { field.setLong(null, Long.parseLong(values.getProperty(key))); }
        }
        return new RequestBodyResources(request, new RequestMultipartResolver());
    }

    /** 只获取流句柄不会触发缓存，认证结束后 Spring 正常读取也不应建缓存。 */
    @Test public void ordinaryJsonAndUnusedHandleNeverCache() throws Exception {
        byte[] bytes = "{\"value\":1}".getBytes(StandardCharsets.UTF_8);
        MockHttpServletRequest request = request("application/json", bytes);
        try (RequestBodyResources resources = resources(request)) {
            resources.forAuthentication(false).getInputStream();
            assertFalse(isBodyCached(resources));
            assertArrayEquals(bytes, resources.forDispatch().getInputStream().readAllBytes());
            assertFalse(isBodyCached(resources));
        }
        assertEquals(0, folder.getRoot().list().length);
    }

    /** 认证只读前缀后，Reader 与后续业务仍从独立游标读到完整内容。 */
    @Test public void partialAuthenticationReadSpillsAndReplays() throws Exception {
        byte[] bytes = "x".repeat(200).getBytes(StandardCharsets.UTF_8);
        try (RequestBodyResources resources = resources(request("application/json", bytes))) {
            HttpServletRequest auth = resources.forAuthentication(false);
            assertEquals('x', auth.getInputStream().read());
            assertTrue(isBodyCached(resources));
            assertTrue(getBodyCacheFile(resources).exists());
            assertEquals(200, auth.getReader().readLine().length());
            assertArrayEquals(bytes, resources.forDispatch().getInputStream().readAllBytes());
        }
        assertEquals(0, folder.getRoot().list().length);
    }

    /** 阈值内请求不创建临时文件，超过阈值才落盘。 */
    @Test public void thresholdBoundary() throws Exception {
        for (int size : new int[]{0, 63, 64, 65}) {
            try (RequestBodyResources resources = resources(request("application/json", new byte[size]))) {
                resources.forAuthentication(false).getInputStream().readAllBytes();
                assertEquals(size > 64, getBodyCacheFile(resources) != null);
            }
        }
        assertEquals(0, folder.getRoot().list().length);
    }

    /** 普通上传只产生解析器管理的文件；重复读取字段和文件不产生 body 缓存。 */
    @Test public void ordinaryUploadParsesOnceWithoutBodyCache() throws Exception {
        try (RequestBodyResources resources = resources(request("multipart/form-data; boundary=probe", multipart(200)))) {
            resources.forAuthentication(false).getHeader("Authorization");
            MultipartHttpServletRequest parsed = (MultipartHttpServletRequest) resources.forDispatch();
            assertSame(parsed, resources.forDispatch());
            assertArrayEquals(new String[]{"first", "中文"}, parsed.getParameterValues("value"));
            assertEquals(200, parsed.getFile("upload").getInputStream().readAllBytes().length);
            assertEquals(200, parsed.getFile("upload").getInputStream().readAllBytes().length);
            assertFalse(isBodyCached(resources));
            assertEquals(1, folder.getRoot().list().length);
            assertEquals(3, resources.items.size());
        }
        assertEquals(0, folder.getRoot().list().length);
    }

    /** 未声明能力的插件先访问参数再读原始流时，保持旧视图并保留后续上传解析。 */
    @Test public void legacyMultipartPluginCanReplayBeforeDispatch() throws Exception {
        byte[] bytes = multipart(200);
        try (RequestBodyResources resources = resources(request("multipart/form-data; boundary=probe", bytes))) {
            HttpServletRequest auth = resources.forAuthentication(false);
            auth.getParameter("query");
            assertTrue(isBodyCached(resources));
            assertArrayEquals(bytes, auth.getInputStream().readAllBytes());
            MultipartHttpServletRequest parsed = (MultipartHttpServletRequest) resources.forDispatch();
            assertEquals(200, parsed.getFile("upload").getSize());
            assertEquals(2, folder.getRoot().list().length);
        }
        assertEquals(0, folder.getRoot().list().length);
    }

    /** 已声明结构化上传能力的认证，头部检查失败前不能触发解析或读取网络。 */
    @Test public void structuredAuthenticationIsLazy() throws Exception {
        try (RequestBodyResources resources = resources(request("multipart/form-data; boundary=probe", multipart(200)))) {
            resources.forAuthentication(true).getHeader("Authorization");
            assertFalse(isBodyCached(resources));
            assertTrue(resources.items.isEmpty());
        }
        assertEquals(0, folder.getRoot().list().length);
    }

    /** 与旧 Commons FileUpload 签名输入对照，覆盖无 charset 字段、重复字段、中文和 query。 */
    @Test public void hmacUploadUsesLegacySignatureWithoutRawCache() throws Exception {
        byte[] bytes = multipart(200);
        MockHttpServletRequest original = request("multipart/form-data; boundary=probe", bytes);
        JSONObject legacy = new JSONObject();
        List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(original);
        try { for (FileItem item : items) { if (item.isFormField()) { legacy.put(item.getFieldName(), item.getString()); } } }
        finally { for (FileItem item : items) { item.delete(); } }
        MockHttpServletRequest request = request("multipart/form-data; boundary=probe", bytes);
        request.setQueryString("query=unchanged");
        request.addHeader("x-access-key", "tester");
        request.addHeader("Authorization", signature(request, legacy.toJSONString()));
        HmacLoginAuthHandler hmac = hmac();
        try (RequestBodyResources resources = resources(request)) {
            HttpServletRequest auth = resources.forAuthentication(hmac.supportsParsedMultipart());
            assertNotNull(hmac.myAuth(auth));
            assertSame(auth, resources.forDispatch());
            assertFalse(isBodyCached(resources));
            assertEquals(1, folder.getRoot().list().length);
        }
        assertEquals(0, folder.getRoot().list().length);
    }

    /** HMAC 普通 body 必须重放，签名失败也能释放已落盘的缓存。 */
    @Test public void hmacJsonReplaysAndFailureCleansUp() throws Exception {
        String body = "{\"value\":\"" + "x".repeat(200) + "\"}";
        for (boolean valid : new boolean[]{true, false}) {
            MockHttpServletRequest request = request("application/json", body.getBytes(StandardCharsets.UTF_8));
            request.addHeader("x-access-key", "tester");
            request.addHeader("Authorization", valid ? signature(request, body) : "Hmac invalid");
            try (RequestBodyResources resources = resources(request)) {
                UserVo user = hmac().myAuth(resources.forAuthentication(true));
                assertEquals(valid, user != null);
                assertTrue(isBodyCached(resources));
                assertEquals(body, new String(resources.forDispatch().getInputStream().readAllBytes(), StandardCharsets.UTF_8));
            }
            assertEquals(0, folder.getRoot().list().length);
        }
    }

    /** 未知长度请求仍按实际字节拒绝，不能通过缺失 Content-Length 绕过。 */
    @Test public void unknownLengthIsLimitedAndFailedCacheDeleted() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest() {
            @Override public long getContentLengthLong() { return -1; }
            @Override public int getContentLength() { return -1; }
        };
        request.getServletContext().setAttribute("javax.servlet.context.tempdir", folder.getRoot());
        request.setContent(new byte[101]);
        try (RequestBodyResources resources = resources(request, "request.body.max.bytes", "100")) {
            RequestBodyException ex = assertThrows(RequestBodyException.class, () -> resources.forAuthentication(false).getInputStream().readAllBytes());
            assertEquals(413, ex.getStatus());
        }
        assertEquals(0, folder.getRoot().list().length);
    }

    /** 字段总量和项数在解析过程中执行限制，失败路径删除已创建的所有项。 */
    @Test public void multipartLimitsCleanPartialFiles() throws Exception {
        for (String[] settings : new String[][]{{"request.multipart.fields.max.bytes", "6"}, {"request.multipart.parts.max", "2"}, {"request.multipart.max.bytes", "100"}}) {
            try (RequestBodyResources resources = resources(request("multipart/form-data; boundary=probe", multipart(200)), settings)) {
                assertEquals(413, assertThrows(RequestBodyException.class, resources::forDispatch).getStatus());
            }
            assertEquals(0, folder.getRoot().list().length);
        }
    }

    /** 首个文件已经落盘后再失败，由 Commons FileUpload 立即清理，不依赖入口兜底删除。 */
    @Test public void parserCleansStoredFileBeforeReturningFailure() throws Exception {
        String prefix = "--probe\r\nContent-Disposition: form-data; name=\"upload\"; filename=\"test.bin\"\r\n\r\n"
                + "x".repeat(200) + "\r\n--probe\r\nContent-Disposition: form-data; name=\"value\"\r\n\r\n"
                + "y".repeat(200);
        String[][] limits = {{"request.multipart.fields.max.bytes", "100"},
                {"request.multipart.parts.max", "1"}, {}};
        for (String[] settings : limits) {
            // 最后一组缺少结束分隔符，覆盖解析到部分文件后才发现报文损坏的路径。
            byte[] bytes = (prefix + (settings.length == 0 ? "" : "\r\n--probe--\r\n"))
                    .getBytes(StandardCharsets.UTF_8);
            try (RequestBodyResources resources = resources(request("multipart/form-data; boundary=probe", bytes), settings)) {
                assertEquals(settings.length == 0 ? 400 : 413,
                        assertThrows(RequestBodyException.class, resources::forDispatch).getStatus());
                assertTrue(resources.items.isEmpty());
                assertEquals(0, folder.getRoot().list().length);
            }
        }
    }

    /** malformed multipart 不能被当成空字段集合继续验签。 */
    @Test public void malformedMultipartFailsClosed() throws Exception {
        try (RequestBodyResources resources = resources(request("multipart/form-data", new byte[0]))) {
            assertEquals(400, assertThrows(RequestBodyException.class, resources::forDispatch).getStatus());
        }
    }

    /** 普通 body 的恰好上限可读，超过上限必须返回资源异常。 */
    @Test public void directReadLimitAndSkipCannotBeBypassed() throws Exception {
        for (int size : new int[]{100, 101}) {
            try (RequestBodyResources resources = resources(request("application/json", new byte[size]), "request.body.max.bytes", "100")) {
                if (size == 100) { assertEquals(100, resources.forDispatch().getInputStream().readAllBytes().length); }
                else { assertEquals(413, assertThrows(RequestBodyException.class, () -> resources.forDispatch().getInputStream().skip(200)).getStatus()); }
                assertFalse(isBodyCached(resources));
            }
        }
    }

    /** 缓存磁盘失败必须保留原因，扩展插件吞异常后也不能放行残缺 body。 */
    @Test public void cacheStorageFailureCannotBeSwallowed() throws Exception {
        File invalidDirectory = folder.newFile("not-a-directory");
        MockHttpServletRequest request = request("application/json", new byte[200]);
        request.getServletContext().setAttribute("javax.servlet.context.tempdir", invalidDirectory);
        try (RequestBodyResources resources = resources(request)) {
            RequestBodyException error = assertThrows(RequestBodyException.class, () -> resources.forAuthentication(false).getInputStream().readAllBytes());
            assertEquals(500, error.getStatus()); assertNotNull(error.getCause());
            assertSame(error, assertThrows(RequestBodyException.class, resources::forDispatch));
        }
        assertEquals(1, folder.getRoot().list().length);
    }

    /** multipart 落盘失败必须映射为存储错误，不能伪装为认证失败或普通格式错误。 */
    @Test public void multipartStorageFailureReturns500() throws Exception {
        File invalidDirectory = folder.newFile("not-a-directory");
        MockHttpServletRequest request = request("multipart/form-data; boundary=probe", multipart(200));
        request.getServletContext().setAttribute("javax.servlet.context.tempdir", invalidDirectory);
        try (RequestBodyResources resources = resources(request)) {
            RequestBodyException error = assertThrows(RequestBodyException.class, resources::forDispatch);
            assertEquals(500, error.getStatus()); assertNotNull(error.getCause());
        }
        assertEquals(1, folder.getRoot().list().length);
    }

    /** 模拟客户端在缓存已落盘后断开连接，验证部分文件删除及失败状态保持。 */
    @Test public void disconnectedClientDeletesPartialCache() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest() {
            @Override public long getContentLengthLong() { return -1; }
            @Override public int getContentLength() { return -1; }
            @Override public ServletInputStream getInputStream() {
                return new SynchronousServletInputStream() {
                    @Override protected InputStream open() {
                        return new InputStream() {
                            private int remaining = 128;
                            @Override public int read() throws IOException {
                                if (remaining-- <= 0) { throw new IOException("模拟客户端断开"); }
                                return 0;
                            }
                            @Override public int read(byte[] bytes, int offset, int length) throws IOException {
                                if (remaining == 0) { throw new IOException("模拟客户端断开"); }
                                int count = Math.min(remaining, length); remaining -= count;
                                Arrays.fill(bytes, offset, offset + count, (byte) 0); return count;
                            }
                        };
                    }
                };
            }
        };
        request.getServletContext().setAttribute("javax.servlet.context.tempdir", folder.getRoot());
        try (RequestBodyResources resources = resources(request)) {
            RequestBodyException error = assertThrows(RequestBodyException.class, () -> resources.forAuthentication(false).getInputStream().readAllBytes());
            assertNotNull(error.getCause());
            assertSame(error, assertThrows(RequestBodyException.class, resources::forDispatch));
        }
        assertEquals(0, folder.getRoot().list().length);
    }

    /** 普通配置 getter 保持原有的六项默认值。 */
    @Test public void defaultConfigurationValues() {
        assertEquals(65536, Config.REQUEST_BODY_MEMORY_THRESHOLD());
        assertEquals(10485760L, Config.REQUEST_BODY_MAX_BYTES());
        assertEquals(1073741824L, Config.REQUEST_MULTIPART_MAX_BYTES());
        assertEquals(40960, Config.REQUEST_MULTIPART_MEMORY_THRESHOLD());
        assertEquals(10485760L, Config.REQUEST_MULTIPART_FIELDS_MAX_BYTES());
        assertEquals(128, Config.REQUEST_MULTIPART_PARTS_MAX());
    }

    /** 已创建请求保留原来的大小和缓存阈值，新请求使用更新后的配置。 */
    @Test public void bodyLimitsRemainFixedDuringRequest() throws Exception {
        try (RequestBodyResources existing = resources(request("application/json", new byte[100]),
                "request.body.max.bytes", "100");
             RequestBodyResources updated = resources(request("application/json", new byte[100]),
                     "request.body.max.bytes", "50", "request.body.memory.threshold", "200")) {
            assertEquals(100, existing.forAuthentication(false).getInputStream().readAllBytes().length);
            assertNotNull(getBodyCacheFile(existing));
            assertEquals(413, assertThrows(RequestBodyException.class, () -> updated.forDispatch().getInputStream().readAllBytes()).getStatus());
        }
        assertEquals(0, folder.getRoot().list().length);
    }

    /** multipart 延迟解析仍使用创建请求时的四项限制，不读取后来变更的全局配置。 */
    @Test public void multipartLimitsRemainFixedBeforeParsing() throws Exception {
        try (RequestBodyResources existing = resources(request("multipart/form-data; boundary=probe", multipart(200)));
             RequestBodyResources updated = resources(request("multipart/form-data; boundary=probe", multipart(200)),
                     "request.multipart.max.bytes", "100", "request.multipart.memory.threshold", "1000",
                     "request.multipart.fields.max.bytes", "1", "request.multipart.parts.max", "1")) {
            MultipartHttpServletRequest parsed = (MultipartHttpServletRequest) existing.forDispatch();
            assertEquals(200, parsed.getFile("upload").getSize());
            assertEquals(1, folder.getRoot().list().length);
            assertEquals(413, assertThrows(RequestBodyException.class, updated::forDispatch).getStatus());
        }
        assertEquals(0, folder.getRoot().list().length);
    }

    /** 用生产默认的 64 KiB 和 40 KiB 分界值确认内存与磁盘存储策略。 */
    @Test public void productionThresholdBoundaries() throws Exception {
        for (int size : new int[]{65535, 65536, 65537}) {
            try (RequestBodyResources resources = resources(request("application/json", new byte[size]), "request.body.memory.threshold", "65536")) {
                assertEquals(size, resources.forAuthentication(false).getInputStream().readAllBytes().length);
                assertEquals(size > 65536, getBodyCacheFile(resources) != null);
            }
            assertEquals(0, folder.getRoot().list().length);
        }
        for (int size : new int[]{40959, 40960, 40961}) {
            try (RequestBodyResources resources = resources(request("multipart/form-data; boundary=probe", multipart(size)), "request.multipart.memory.threshold", "40960")) {
                MultipartHttpServletRequest parsed = (MultipartHttpServletRequest) resources.forDispatch();
                assertEquals(size, parsed.getFile("upload").getSize());
                assertFalse(isBodyCached(resources));
                assertEquals(size > 40960 ? 1 : 0, folder.getRoot().list().length);
            }
            assertEquals(0, folder.getRoot().list().length);
        }
    }

    /** 使用内存用户服务替身，避免签名测试访问真实数据库或签发会话。 */
    static HmacLoginAuthHandler hmac() throws Exception {
        HmacLoginAuthHandler handler = new HmacLoginAuthHandler();
        UserVo user = new UserVo(); user.setUuid("tester");
        Field field = HmacLoginAuthHandler.class.getDeclaredField("userService"); field.setAccessible(true);
        field.set(handler, Proxy.newProxyInstance(UserService.class.getClassLoader(), new Class[]{UserService.class},
                (proxy, method, args) -> method.getName().equals("getUserByUser") ? user
                        : method.getName().equals("getUserTokenByUser") ? "test-secret" : null));
        return handler;
    }

    /** 旧签名格式作为兼容基线，不能改成参数 Map 的序列化。 */
    static String signature(HttpServletRequest request, String body) {
        String query = request.getQueryString() == null ? "" : "?" + request.getQueryString();
        return "Hmac " + SHA256Util.encrypt("test-secret", "tester#" + request.getRequestURI() + query + "#"
                + Base64.getEncoder().encodeToString(body.getBytes(StandardCharsets.UTF_8)));
    }
}
