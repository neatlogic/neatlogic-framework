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

    public static Map<String, LicenseInvalidVo> licenseInvalidTipsMap = new HashMap<>();

    public static Map<String, ILicensePolicy> licensePolicyMap = new HashMap<>();
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
}
