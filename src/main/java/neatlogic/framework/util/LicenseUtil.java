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
import org.springframework.util.Base64Utils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class LicenseUtil {
    static Logger logger = LoggerFactory.getLogger(LicenseUtil.class);

    /**
     * 根据租户设置license
     *
     * @param licenseStr license串
     */
    public static LicenseVo deLicense(String licenseStr, String licencePK) {
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

    public static void main(String[] a) {
        String s = "gWKDaBcSaeAXcCXlrO7nLHbcYfqeIsZgxkreDIlGXamjfEAvQVHtFUWsbwiANl1jkRMXkum7auBF1qfSsfd4IkXREBj1WHumHqhqZRlkfyM37UDtdyd6x/hUri3/quYiwocPnYuXcWXcrN9Oko76dv1giDptNzceteTCASu+v0s9I42RLWTHMbbUN+tufU42yqTHVsbz0H69iPtDssRhJ4NcLaWM7Do5apCRlZsxheJAH3qsBFWu5zvCUEOHlzVtW7II/AkiIDtVUDEJoGctvzcsWvlxtLfRbq1kGnzi+q92ryex5LKBf62z/eiBTa3LDsEpS9ki0rnNOlPCOUHd7g==#HNsh1a4RNkkJIrXMf4VhNzVtusjwK8PkzP9A8rU154DBvRuF6tawreg3xJiYYB1yDI5bjk1xUmjfv+3m0X8ExLd+XrAQfZWoLxQTvff8g26kIpYVmxWS7M+sIyj3Qlgu+NbJHG8K1YJ+vOOLtfJMBghla3qkSATrbWAOqw5hbB4=";
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDHfhFEod4K3VlDdF0zGFLeavGyokUt/Njczl/4yAwpOvTUNW0x3C4Uc49NjP3Vr8KYYbsXk7DKv+8hoENGfltSzepmPpX4Mu+iclUqyaLbLHoX+rB7SagjiekuG3B1/ADPLsuG6VFkC00yEFkMUmZhU5IVbTTTKEpCtH8bJxa1aQIDAQAB";
        System.out.println(String.join(",", Objects.requireNonNull(deLicense(s, publicKey)).getModules()));
    }

}
