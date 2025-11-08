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
import neatlogic.framework.common.constvalue.license.ILicensePolicy;
import neatlogic.framework.dto.license.LicenseInvalidVo;
import neatlogic.framework.dto.license.LicenseVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LicenseUtil {
    static Logger logger = LoggerFactory.getLogger(LicenseUtil.class);

    public static Map<String, Map<String,LicenseInvalidVo>> tenantLicenseInvalidTipsMap = new HashMap<>();

    public static Map<String, ILicensePolicy> licensePolicyMap = new HashMap<>();

    /**
     * 根据租户设置license
     *
     * @param licenseStr license串
     */
    public static LicenseVo deLicense(String licenseStr, String licencePK) {
        if(StringUtils.isBlank(licenseStr)){
            return null;
        }
        licenseStr = licenseStr.replaceAll("\\r\\n", StringUtils.EMPTY).replaceAll("\\n", StringUtils.EMPTY).trim();
        String[] licenses = licenseStr.split("#");
        if (licenses.length != 2) {
            logger.error("license invalid (length):" + licenseStr);
            return null;
        }
        String sign = licenses[1];
        byte[] decodeData = licenses[0].getBytes(StandardCharsets.UTF_8);
        if (StringUtils.isBlank(licencePK)) {
            logger.error("license pk is blank:" + licenseStr);
            return null;
        }
        if (!RSAUtils.verify(decodeData, licencePK, sign)) {
            logger.error("license invalid (verify):" + licenseStr);
            return null;
        }
        String license = new String(Objects.requireNonNull(RSAUtils.decryptByPublicKey(Base64Utils.decode(decodeData), licencePK)), StandardCharsets.UTF_8);
        return JSONObject.parseObject(license).toJavaObject(LicenseVo.class);
    }
}
