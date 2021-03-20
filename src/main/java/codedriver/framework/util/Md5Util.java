package codedriver.framework.util;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @ClassName: EncryptUtil
 * @Description: 加密工具类
 * @author: fandong
 */
public class Md5Util {

    private static final Logger logger = LoggerFactory.getLogger(Md5Util.class);

    private static final String DEFAULT_CHARSET = "UTF-8";

    private static Md5Util instance = null;

    private Md5Util() {

    }

    //single
    public static Md5Util getInstance() {
        if (instance == null)
            instance = new Md5Util();
        return instance;
    }


    /*
     * @Description: MD5加密 32位 小写
     * @Author: chenqiwei
     * @Date: 2021/3/18 12:20 下午
     * @Params: [content]
     * @Returns: java.lang.String
     **/
    public static String encryptMD5(String content) {
        try {
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(content.getBytes());
            byte[] md = mdInst.digest();
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < md.length; i++) {
                String shaHex = Integer.toHexString(md[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }


    /**
     * @param content
     * @return String
     * @Description: BASE64加密  默认base64编码
     */
    public static String encryptBASE64(String content) {
        return encryptBASE64(content, DEFAULT_CHARSET);
    }

    public static String encryptBASE64(byte[] bytes) {
        return new String(Base64.encodeBase64String(bytes));
    }

    public static String encryptBASE64(String content, String charset) {
        if (content == null) {
            return null;
        }
        try {
            byte[] bytes = content.getBytes(charset);
            return encryptBASE64(bytes);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        System.out.println(encryptMD5("admin"));
    }

}
