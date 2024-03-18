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
