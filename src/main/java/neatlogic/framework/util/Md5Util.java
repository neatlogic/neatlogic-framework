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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public class Md5Util {

    private static final Logger logger = LoggerFactory.getLogger(Md5Util.class);


    private Md5Util() {

    }


    /*
     * @Description: MD5加密 32位 小写
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
            for (byte b : md) {
                String shaHex = Integer.toHexString(b & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
        }
        return "00000000000000000000000000000000";
    }

    public static boolean isMd5(String content) {
        if (StringUtils.isNotBlank(content)) {
            if (content.length() == 32) {
                Pattern command = Pattern.compile("^[A-Fa-f0-9]+$");
                return command.matcher(content).matches();
            }
        }
        return false;
    }


}
