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
import neatlogic.framework.dto.LicenseVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Objects;

public class LicenseUtil {
    static Logger logger = LoggerFactory.getLogger(LicenseUtil.class);

    /**
     * 根据租户设置license
     *
     * @param licenseStr license串
     */
    public static String deLicense(String licenseStr, String licencePK, String dbUrl) {
        licenseStr = licenseStr.replaceAll("\\r\\n", StringUtils.EMPTY).replaceAll("\\n", StringUtils.EMPTY).trim();
        String[] licenses = licenseStr.split("#");
        if (licenses.length != 2) {
            logger.error("license invalid (length):" + licenseStr);
            System.exit(1);
        }
        String sign = licenses[1];
        byte[] decodeData = Base64.getDecoder().decode(licenses[0].getBytes(Charset.forName("GBK")));
        if (StringUtils.isBlank(licencePK)) {
            logger.error("license pk is blank:" + licenseStr);
            System.exit(1);
        }
        if (!RSAUtils.verify(decodeData, licencePK, sign)) {
            logger.error("license invalid (verify):" + licenseStr);
            System.exit(1);
        }
        String license = new String(Objects.requireNonNull(RSAUtils.decryptByPublicKey(decodeData, licencePK)), Charset.forName("GBK"));
        LicenseVo licenseVo = JSONObject.parseObject(license).toJavaObject(LicenseVo.class);
        //判断数据库连接串是否匹配
        if (!dbUrl.startsWith(licenseVo.getDbUrl())) {
            logger.error("license invalid (dbUrl):" + licenseStr);
            System.exit(1);
        }
        //判断是否停止服务
        long diffTime = licenseVo.getExpireTime().getTime() - System.currentTimeMillis();
        if (licenseVo.isExpiredOutOfDay(diffTime)) {
            logger.error("license is expired:" + licenseStr);
            System.exit(1);
        }
        return JSONObject.toJSONString(licenseVo);
    }

}
