package neatlogic.framework.util;

import neatlogic.framework.crypto.core.CryptoHandlerFactory;
import neatlogic.framework.crypto.core.ICryptoHandler;
import neatlogic.framework.crypto.handler.NoCryptoHandler;
import neatlogic.framework.crypto.handler.RC4CryptoHandler;
import neatlogic.framework.exception.util.PasswordDecryptException;
import org.apache.commons.lang3.StringUtils;

/** 三类基础连接配置共用的密码转换规则。 */
public final class ConnectionPasswordUtil {
    private ConnectionPasswordUtil() {
    }

    /** 通过统一处理器判断是否为已支持的密文，明文处理器不算加密。 */
    public static boolean isEncrypted(String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        return !(CryptoHandlerFactory.getCryptoHandlerByCiphertext(value) instanceof NoCryptoHandler);
    }

    /** 解密已识别的密文；损坏密文报错，历史明文直接返回。 */
    public static String decrypt(String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }
        ICryptoHandler handler = CryptoHandlerFactory.getCryptoHandlerByCiphertext(value);
        try {
            // RC4工具对奇数长度会截断，连接密码必须先校验完整的十六进制载荷。
            if (handler instanceof RC4CryptoHandler) {
                String payload = value.substring(value.indexOf('}') + 1);
                if (value.startsWith("RC4:")) {
                    payload = value.substring(4);
                }
                if (payload.isEmpty() || payload.length() % 2 != 0 || !payload.matches("[0-9a-fA-F]+")) {
                    throw new PasswordDecryptException();
                }
            }
            return handler.decrypt(value);
        } catch (RuntimeException e) {
            // 不保留底层异常中的载荷片段，避免错误日志泄露密码。
            throw new PasswordDecryptException();
        }
    }

    /** 显式明文使用现有混合加密，连接存储统一输出AES前缀。 */
    public static String encryptPlain(String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }
        String storagePrefix = "AES:";
        ICryptoHandler handler = CryptoHandlerFactory.getCryptoHandlerByHandler(storagePrefix);
        if (handler instanceof NoCryptoHandler) {
            throw new IllegalStateException("RSA.AES密码处理器未加载");
        }
        String cipher = handler.encrypt(value);
        // 通过处理器声明的列表识别输出前缀，存储层不再依赖某个传输别名及其长度。
        for (String prefix : handler.handlers()) {
            if (cipher.startsWith(prefix)) {
                return storagePrefix + cipher.substring(prefix.length());
            }
        }
        throw new IllegalStateException("密码处理器返回了未声明的密文前缀");
    }

    /** 保留已支持密文；历史明文转换为默认存储格式。 */
    public static String toCipher(String value) {
        if (isEncrypted(value)) {
            return value;
        }
        return encryptPlain(value);
    }
}
