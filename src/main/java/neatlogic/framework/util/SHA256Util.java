/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.util;

import org.apache.commons.net.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class SHA256Util {

    private static final Logger logger = LoggerFactory.getLogger(SHA256Util.class);


    private SHA256Util() {

    }


    /*
     * @Description: Sha1签名加密 32位 小写
     * @Date: 2021/3/18 12:20 下午
     * @Params: [content]
     * @Returns: java.lang.String
     **/
    public static String encrypt(String secret, String sign) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(sign.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : rawHmac) {
                String shaHex = Integer.toHexString(b & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            logger.error(e.getMessage(), e);
        }
        return "0000000000000000000000000000000000000000000000000000000000000000";
    }

    public static void main(String[] a) {
        System.out.println(encrypt("abd", "admin#/neatlogic/api/rest/testhmac#" + Base64.encodeBase64StringUnChunked("{\"id\":379606793519104,\"ciEntityId\":371583055765506}".getBytes(StandardCharsets.UTF_8))));
    }


}
