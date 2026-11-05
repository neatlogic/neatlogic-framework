package neatlogic.module.framework.filter.handler;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.config.ConfigManager;
import neatlogic.framework.dao.mapper.ConfigMapper;
import neatlogic.framework.dto.UserVo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import static org.junit.Assert.*;

/** 使用真实签名验证 Cookie 边界和 Header 优先级，不依赖数据库或外部登录服务。 */
public class DefaultLoginAuthHandlerTest {
    private static final String SECRET = "cookie-boundary-test-secret";
    private final Map<Field, Object> originals = new LinkedHashMap<>();
    private final DefaultLoginAuthHandler handler = new DefaultLoginAuthHandler();

    /** 只替换签名密钥与租户配置查询，其他认证逻辑使用真实实现。 */
    @Before public void setUp() throws Exception {
        replace(Config.class, "JWT_SECRET", SECRET);
        replace(ConfigManager.class, "configMapper", Proxy.newProxyInstance(ConfigMapper.class.getClassLoader(),
                new Class<?>[]{ConfigMapper.class}, (proxy, method, args) -> null));
    }

    /** 恢复静态依赖，避免影响其他模块内测试。 */
    @After public void tearDown() throws Exception {
        for (Map.Entry<Field, Object> entry : originals.entrySet()) { entry.getKey().set(null, entry.getValue()); }
    }

    /** 使用合法历史明文 JWT 覆盖完整 Cookie 长度边界，不能因签名无效掩盖限额判断。 */
    @Test public void limitsCompleteUncompressedCookie() throws Exception {
        for (int length : new int[]{16383, 16384, 16385}) {
            UserVo user = handler.myAuth(request(signedToken(length)));
            if (length <= 16384) { assertEquals("cookie-user", user.getUuid()); }
            else { assertNull(user); }
        }
    }

    /** 压缩后很小但解压后超过上限的正确签名 JWT 也必须拒绝。 */
    @Test public void limitsDecompressedSignedCookie() throws Exception {
        for (int length : new int[]{65535, 65536, 65537}) {
            String cookie = compressedCookie(signedToken(length));
            assertTrue(cookie.length() < 16384);
            UserVo user = handler.myAuth(request(cookie));
            if (length <= 65536) { assertEquals("cookie-user", user.getUuid()); }
            else { assertNull(user); }
        }
    }

    /** 编码长度包括 GZIP_ 前缀；在合法 GZIP 成员后补零以独立控制编码长度。 */
    @Test public void countsGzipPrefixBeforeDecoding() throws Exception {
        String token = signedToken(300);
        byte[] gzip = Base64.getDecoder().decode(compressedCookie(token).substring(5));
        // 无填充 Base64 的长度可精确达到剩余字符额度，JDK 接受合法成员后的零填充。
        for (int length : new int[]{16383, 16384, 16385}) {
            int encodedLength = length - 5;
            byte[] padded = java.util.Arrays.copyOf(gzip, encodedLength * 3 / 4);
            String cookie = "GZIP_" + Base64.getEncoder().withoutPadding().encodeToString(padded);
            assertEquals(length, cookie.length());
            UserVo user = handler.myAuth(request(cookie));
            if (length <= 16384) { assertEquals("cookie-user", user.getUuid()); }
            else { assertNull(user); }
        }
    }

    /** 有效 Header 不读取坏 Cookie，既有 Header 身份必须完整保留。 */
    @Test public void headerTakesPriorityOverRejectedCookies() throws Exception {
        for (String cookie : new String[]{"x".repeat(16385), "GZIP_%%%", compressedCookie("x".repeat(2 * 1024 * 1024))}) {
            MockHttpServletRequest request = request(cookie);
            request.addHeader("Authorization", signedToken(300));
            UserVo user = handler.myAuth(request);
            assertEquals("cookie-user", user.getUuid());
            assertNull(user.getCookieAuthorization());
        }
    }

    /** 坏 Cookie 直接产生无效认证结果，不能作为原文或残缺内容继续认证。 */
    @Test public void rejectsMalformedAndHighlyCompressedCookies() throws Exception {
        String bomb = compressedCookie("x".repeat(2 * 1024 * 1024));
        assertTrue(bomb.length() < 4096);
        for (String cookie : new String[]{"GZIP_", "GZIP_%%%", "GZIP_eA==", bomb}) {
            assertNull(handler.myAuth(request(cookie)));
        }
    }

    /** 生成具有指定精确长度的正确签名 JWT，JSON 尾部空白仅用于控制长度。 */
    private static String signedToken(int length) throws Exception {
        JSONObject body = new JSONObject();
        body.put("useruuid", "cookie-user"); body.put("userid", "tester");
        body.put("username", "测试用户"); body.put("tenant", "cookie-test");
        body.put("createTime", 1000L); body.put("isSuperAdmin", false);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        String json = body.toJSONString();
        int bodyBytes = json.getBytes(StandardCharsets.UTF_8).length;
        for (int extra = 0; extra < 3; extra++) {
            String header = encoder.encodeToString(("{\"alg\":\"HS256\",\"typ\":\"JWT\"}" + " ".repeat(extra)).getBytes(StandardCharsets.UTF_8));
            int targetBodyLength = length - 7 - header.length() - 2 - 28;
            int padding = targetBodyLength * 3 / 4 - bodyBytes;
            if (padding < 0) { continue; }
            String payload = encoder.encodeToString((json + " ".repeat(padding)).getBytes(StandardCharsets.UTF_8));
            if (payload.length() != targetBodyLength) { continue; }
            String content = header + "." + payload;
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
            String token = "Bearer_" + content + "." + Base64.getUrlEncoder().encodeToString(mac.doFinal(content.getBytes(StandardCharsets.UTF_8)));
            assertEquals(length, token.length());
            return token;
        }
        throw new AssertionError("无法构造指定长度的 JWT: " + length);
    }

    /** 独立用 JDK 生成 Cookie，避免将生产解压方法用作测试输入生成器。 */
    private static String compressedCookie(String value) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(bytes)) { gzip.write(value.getBytes(StandardCharsets.UTF_8)); }
        return "GZIP_" + Base64.getEncoder().encodeToString(bytes.toByteArray());
    }

    /** 请求仅携带内存 Cookie 和测试租户，不访问应用服务。 */
    private static MockHttpServletRequest request(String cookie) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Tenant", "cookie-test");
        request.setCookies(new Cookie("neatlogic_authorization", cookie));
        return request;
    }

    /** 保存并替换测试范围内的静态依赖。 */
    private void replace(Class<?> type, String name, Object value) throws Exception {
        Field field = type.getDeclaredField(name); field.setAccessible(true);
        originals.put(field, field.get(null)); field.set(null, value);
    }
}
