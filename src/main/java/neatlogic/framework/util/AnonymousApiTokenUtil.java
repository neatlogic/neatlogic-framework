/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.util;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.util.RC4Util;
import org.apache.commons.lang3.StringUtils;

public class AnonymousApiTokenUtil {

    public static String encrypt(String source) {
        if (StringUtils.isBlank(source)) {
            return source;
        }
        //source = api/binary/image/download?id=314907690737664
        System.out.println("source=" + source);
        String[] split = source.split("\\?");
        String path = split[0];
        String queryString = split[1];
        String tenant = TenantContext.get().getTenantUuid();
        int fromIndex = path.indexOf("api/");
        int beginIndex = fromIndex;
        fromIndex += 4;
        int endIndex = path.indexOf("/", fromIndex) + 1;
        String token = path.substring(endIndex);
        String encryptedData = RC4Util.encrypt(token + "/" + tenant + "?" + queryString);
        String backEndUrl = Config.BACK_END_URL();
        if(StringUtils.isNotBlank(backEndUrl)) {
            if (!backEndUrl.endsWith("/")) {
                backEndUrl += "/";
            }
        } else {
            backEndUrl = "";
        }
        String target = backEndUrl + "anonymous/" + path.substring(beginIndex, endIndex) + encryptedData;
        return target;
    }

    public static JSONObject decrypt(String token) {
        JSONObject resultObj = new JSONObject();
        String decryptData = RC4Util.decrypt(token);
        String[] split = decryptData.split("\\?", 2);
        token = split[0].substring(0, split[0].lastIndexOf("/"));
        String tenant = split[0].substring(split[0].lastIndexOf("/") + 1);
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
        resultObj.put("tenant", tenant);
        resultObj.put("paramObj", paramObj);
        return resultObj;
    }
}
