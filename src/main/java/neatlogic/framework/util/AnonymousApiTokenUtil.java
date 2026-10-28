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

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.util.RC4Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.Base64;

import java.nio.charset.StandardCharsets;

public class AnonymousApiTokenUtil {

    public static String encrypt(String source) {
        if (StringUtils.isBlank(source)) {
            return source;
        }
        String path = source;
        //source = api/binary/image/download?id=31490769073766
        String tenant = TenantContext.get().getTenantUuid();
        int fromIndex = path.indexOf("api/");
        int beginIndex = fromIndex;
        fromIndex += 4;
        int endIndex = path.indexOf("/", fromIndex) + 1;
        String token = path.substring(endIndex);
        String encryptedData = RC4Util.encrypt(token);
        String[] split = path.substring(beginIndex, endIndex).split("/");
        String api = split[0];
        String apiType = split[1];
        String target = "anonymous/" + api + "/t/" + tenant + "/" + apiType + "/" + encryptedData;
        return target;
    }

    public static JSONObject decrypt(String token) {
        JSONObject resultObj = new JSONObject();
        String decryptData = RC4Util.decrypt(token);
        String[] split = decryptData.split("\\?", 2);
        token = split[0];
        JSONObject paramObj = new JSONObject();
        if (split.length == 2) {
            String[] params = split[1].split("&");
            for (String param : params) {
                String[] array = param.split("=", 2);
                if (array.length == 2) {
                    paramObj.put(array[0], array[1]);
                }
            }
        }
        resultObj.put("token", token);
        resultObj.put("paramObj", paramObj);
        return resultObj;
    }

    public static String getSign(String token) {
        String[] split = token.split("\\?", 2);
        token = split[0];
        JSONObject paramObj = new JSONObject();
        if (split.length == 2) {
            String[] params = split[1].split("&");
            for (String param : params) {
                String[] array = param.split("=", 2);
                if (array.length == 2) {
                    paramObj.put(array[0], array[1]);
                }
            }
        }
        return getSign(token, paramObj);
    }

    public static String getSign(String token, JSONObject paramObj) {
        String paramStr = JSONObject.toJSONString(paramObj, SerializerFeature.MapSortField);
        String str = token + "#" + Base64.encodeBase64StringUnChunked(paramStr.getBytes(StandardCharsets.UTF_8));
        String encryptMD5 = Md5Util.encryptMD5To16(str);
        return encryptMD5;
    }
}
