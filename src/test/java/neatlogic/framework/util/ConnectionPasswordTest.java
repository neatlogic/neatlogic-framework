package neatlogic.framework.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.util.RC4Util;
import neatlogic.framework.crypto.core.CryptoHandlerFactory;
import neatlogic.framework.crypto.handler.RSAAESCryptoHandler;
import neatlogic.framework.crypto.handler.RSACryptoHandler;
import neatlogic.framework.dto.DatasourceVo;
import neatlogic.framework.dto.ElasticsearchVo;
import neatlogic.framework.dto.MongoDbVo;
import neatlogic.framework.exception.util.PasswordDecryptException;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;
import org.junit.Test;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.*;

/** 从数据库映射字段到认证明文及保存参数验证三类连接的密码契约。 */
public class ConnectionPasswordTest {
    private static final List<Class<?>> TYPES = List.of(DatasourceVo.class, MongoDbVo.class, ElasticsearchVo.class);
    private static final String PASSWORD = "连接Password@123";

    /** 各类密文保持原样，历史明文仅在保存时升级，重复读取不会重复加密。 */
    @Test
    public void readsAllFormatsAndStoresStableCipher() throws Exception {
        String rc4 = RC4Util.encrypt(PASSWORD);
        List<String> values = List.of(PASSWORD, rc4, rc4.replace("{RC4}", "RC4:"),
                rc4.replace("{RC4}", "{ENCRYPTED}"), new RSACryptoHandler().encrypt(PASSWORD),
                new RSAAESCryptoHandler().encrypt(PASSWORD), ConnectionPasswordUtil.encryptPlain(PASSWORD),
                "{AES}" + ConnectionPasswordUtil.encryptPlain(PASSWORD).substring("AES:".length()));
        for (Class<?> type : TYPES) {
            for (String value : values) {
                Object vo = type.getDeclaredConstructor().newInstance();
                set(vo, "Cipher", value);
                assertEquals(PASSWORD, get(vo, "Plain"));
                String cipher = get(vo, "Cipher");
                if (value.equals(PASSWORD)) {
                    assertTrue(cipher.startsWith("AES:"));
                } else {
                    assertEquals(value, cipher);
                }
                assertEquals(cipher, get(vo, "Cipher"));
                assertEquals(PASSWORD, ConnectionPasswordUtil.decrypt(cipher));
                assertEquals(PASSWORD, get(vo, "Plain"));
            }
        }
    }

    /** 同一载荷可使用列表声明的任一前缀，工厂识别及直接解密保持一致。 */
    @Test
    public void aesAliasesUseOnePrefixList() {
        RSAAESCryptoHandler handler = new RSAAESCryptoHandler();
        String encrypted = handler.encrypt(PASSWORD);
        assertEquals("RSA.AES:", RSAAESCryptoHandler.ENCRYPTED_PREFIX);
        assertTrue(encrypted.startsWith(RSAAESCryptoHandler.ENCRYPTED_PREFIX));
        String payload = encrypted.substring(RSAAESCryptoHandler.ENCRYPTED_PREFIX.length());
        assertEquals(List.of(RSAAESCryptoHandler.ENCRYPTED_PREFIX, "AES:", "{AES}"), handler.handlers());
        for (String prefix : handler.handlers()) {
            String alias = prefix + payload;
            assertTrue(CryptoHandlerFactory.getCryptoHandlerByCiphertext(alias) instanceof RSAAESCryptoHandler);
            assertEquals(PASSWORD, handler.decrypt(alias));
            assertEquals(PASSWORD, ConnectionPasswordUtil.decrypt(alias));
            assertEquals(alias, ConnectionPasswordUtil.toCipher(alias));
        }
        assertThrows(PasswordDecryptException.class, () -> handler.decrypt("UNKNOWN:" + payload));
    }

    /** 修改任意密码表示均清除旧缓存；明文中的协议前缀也按真实密码保存。 */
    @Test
    public void settersInvalidateCachedRepresentation() throws Exception {
        for (Class<?> type : TYPES) {
            Object vo = type.getDeclaredConstructor().newInstance();
            set(vo, "Plain", PASSWORD);
            String original = get(vo, "Cipher");
            set(vo, "Plain", "RSA:literal-password");
            String changed = get(vo, "Cipher");
            assertNotEquals(original, changed);
            assertEquals("RSA:literal-password", ConnectionPasswordUtil.decrypt(changed));
            set(vo, "Cipher", new RSAAESCryptoHandler().encrypt("new-password"));
            assertEquals("new-password", get(vo, "Plain"));
            set(vo, "Plain", null);
            assertNull(get(vo, "Plain"));
            assertNull(get(vo, "Cipher"));
            set(vo, "Plain", "");
            assertEquals("", get(vo, "Plain"));
            assertNull(get(vo, "Cipher"));
            set(vo, "Cipher", "");
            assertNull(get(vo, "Plain"));
            assertEquals("", get(vo, "Cipher"));
        }
    }

    /** JSON仅输出密文，防止历史明文通过passwordCipher字段原样泄露。 */
    @Test
    public void jsonDoesNotExposePlaintext() throws Exception {
        for (Class<?> type : TYPES) {
            Object vo = type.getDeclaredConstructor().newInstance();
            if (vo instanceof DatasourceVo) {
                ((DatasourceVo) vo).setHost("127.0.0.1");
                ((DatasourceVo) vo).setPort(3306);
            }
            set(vo, "Cipher", PASSWORD);
            get(vo, "Plain");
            JSONObject json = JSON.parseObject(JSON.toJSONString(vo));
            assertFalse(json.containsKey("passwordPlain"));
            assertTrue(json.getString("passwordCipher").startsWith("AES:"));
            assertFalse(json.toJSONString().contains(PASSWORD));
        }
    }

    /** 损坏的已知密文必须失败，禁止当作明文连接远端。 */
    @Test
    public void malformedCipherFailsClosed() throws Exception {
        for (Class<?> type : TYPES) {
            for (String value : List.of("{RC4}a", "RC4:zz", "{ENCRYPTED}", "RSA:invalid", "RSA.AES:broken", "AES:broken", "{AES}broken")) {
                Object vo = type.getDeclaredConstructor().newInstance();
                set(vo, "Cipher", value);
                try {
                    get(vo, "Plain");
                    fail("损坏密文不应被接受");
                } catch (java.lang.reflect.InvocationTargetException e) {
                    assertTrue(e.getCause() instanceof PasswordDecryptException);
                }
            }
        }
    }

    /** 解析真实MyBatis映射，确认INSERT及重复键UPDATE均绑定密文参数。 */
    @Test
    public void mapperInsertAndUpdateBindCipher() throws Exception {
        Configuration configuration = new Configuration();
        String[] mappers = {"Datasource", "MongoDb", "Elasticsearch"};
        String[] methods = {"insertDatasource", "insertTenantMongodb", "insertTenantElasticsearch"};
        for (int i = 0; i < mappers.length; i++) {
            String path = "neatlogic/framework/dao/mapper/" + mappers[i] + "Mapper.xml";
            try (InputStream stream = getClass().getClassLoader().getResourceAsStream(path)) {
                assertNotNull(stream);
                new XMLMapperBuilder(stream, configuration, path, configuration.getSqlFragments()).parse();
            }
            Object vo = TYPES.get(i).getDeclaredConstructor().newInstance();
            set(vo, "Plain", PASSWORD);
            BoundSql sql = configuration.getMappedStatement("neatlogic.framework.dao.mapper."
                    + mappers[i] + "Mapper." + methods[i]).getBoundSql(vo);
            long count = sql.getParameterMappings().stream().map(ParameterMapping::getProperty)
                    .filter("passwordCipher"::equals).count();
            assertEquals(2, count);
            assertFalse(sql.getParameterMappings().stream().anyMatch(mapping -> "passwordPlain".equals(mapping.getProperty())));
            assertEquals(PASSWORD, ConnectionPasswordUtil.decrypt(get(vo, "Cipher")));
        }
    }

    /** 通过统一属性名称覆盖三个独立Vo，避免测试复制。 */
    private static void set(Object vo, String kind, String value) throws Exception {
        Method method = vo.getClass().getMethod("setPassword" + kind, String.class);
        method.invoke(vo, value);
    }

    /** 读取认证或保存使用的真实属性方法。 */
    private static String get(Object vo, String kind) throws Exception {
        return (String) vo.getClass().getMethod("getPassword" + kind).invoke(vo);
    }
}
