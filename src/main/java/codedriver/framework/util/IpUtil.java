/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author lvzk
 * @since 2021/4/25 17:19
 **/
public  class IpUtil {
    /**
     * 判断ip是否属于某个网段
     * @param ip 判断ip
     * @param targetIp 网段ip
     * @param subnetMask 掩码
     * @return boolean
     */
    public static boolean isBelongSegment(String ip, String targetIp, int subnetMask) {
        if (StringUtils.isBlank(ip)) {
            return false;
        }
        String cidr = targetIp + "/" + subnetMask;
        String[] ips = ip.split("\\.");
        int ipAddress = (Integer.parseInt(ips[0]) << 24) | (Integer.parseInt(ips[1]) << 16) | (Integer.parseInt(ips[2]) << 8) | Integer.parseInt(ips[3]);
        int type = Integer.parseInt(cidr.replaceAll(".*/", ""));
        int mask = 0xFFFFFFFF << (32 - type);
        String cidrIp = cidr.replaceAll("/.*", "");
        String[] cidrIps = cidrIp.split("\\.");
        int cidrIpAddress = (Integer.parseInt(cidrIps[0]) << 24) | (Integer.parseInt(cidrIps[1]) << 16) | (Integer.parseInt(cidrIps[2]) << 8) | Integer.parseInt(cidrIps[3]);
        return (ipAddress & mask) == (cidrIpAddress & mask);
    }
}
