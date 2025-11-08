/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

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
