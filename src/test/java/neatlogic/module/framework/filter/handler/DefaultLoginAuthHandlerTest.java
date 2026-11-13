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

/** 使用真实签名验证 Cookie 认证和 Header 优先级，不依赖数据库或外部登录服务。 */
public class DefaultLoginAuthHandlerTest {
    private static final String SECRET = "cookie-auth-test-secret";
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

    /** 正确签名的明文和压缩 Cookie 均可认证，不约束编码或解压后的长度。 */
    @Test public void acceptsPlainAndCompressedCookies() throws Exception {
        String token = signedToken();
        for (String cookie : new String[]{token, compressedCookie(token)}) {
            UserVo user = handler.myAuth(request(cookie));
            assertEquals("cookie-user", user.getUuid());
            assertEquals(cookie, user.getCookieAuthorization());
        }
    }

    /** 有效 Header 不读取坏 Cookie，既有 Header 身份必须完整保留。 */
    @Test public void headerTakesPriorityOverMalformedCookies() throws Exception {
        for (String cookie : new String[]{"invalid-token", "GZIP_%%%", compressedCookie("invalid-token")}) {
            MockHttpServletRequest request = request(cookie);
            request.addHeader("Authorization", signedToken());
            UserVo user = handler.myAuth(request);
            assertEquals("cookie-user", user.getUuid());
            assertNull(user.getCookieAuthorization());
        }
    }

    /** 无效 Cookie 不产生用户身份；myAuth 返回空身份对象，由认证入口判定无效。 */
    @Test public void rejectsMalformedCookies() throws Exception {
        for (String cookie : new String[]{"GZIP_", "GZIP_%%%", "GZIP_eA==", compressedCookie("invalid-token")}) {
            UserVo user = handler.myAuth(request(cookie));
            assertTrue(user == null || user.getUuid() == null);
        }
    }

    /** 生成正确签名的历史格式 JWT，不使用额外填充构造长度边界。 */
    private static String signedToken() throws Exception {
        JSONObject body = new JSONObject();
        body.put("useruuid", "cookie-user"); body.put("userid", "tester");
        body.put("username", "测试用户"); body.put("tenant", "cookie-test");
        body.put("createTime", 1000L); body.put("isSuperAdmin", false);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        String header = encoder.encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
        String payload = encoder.encodeToString(body.toJSONString().getBytes(StandardCharsets.UTF_8));
        String content = header + "." + payload;
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
        return "Bearer_" + content + "." + Base64.getUrlEncoder().encodeToString(mac.doFinal(content.getBytes(StandardCharsets.UTF_8)));
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
