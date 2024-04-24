package neatlogic.framework.common.util;

import neatlogic.framework.exception.runner.IPIsIncorrectException;
import neatlogic.framework.exception.util.IpSubnetMaskException;
import neatlogic.framework.util.RegexUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

public class IpUtil {
    private IpUtil() {

    }

    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 判断ip是否属于某个网段
     *
     * @param ip         判断ip
     * @param networkIp   网段ip
     * @param subnetMask 掩码
     * @return boolean
     */
    public static boolean isBelongSegment(String ip, String networkIp, int subnetMask) throws IpSubnetMaskException {
        if (StringUtils.isEmpty(ip)) {
            return false;
        }
        if(!RegexUtils.isMatch(ip,RegexUtils.IP)){
            throw new IPIsIncorrectException(ip);
        }
        if(subnetMask == 0){ //掩码为0则表示匹配所有ip -by波哥
            return true;
        }
        try {
            String cidr = networkIp + "/" + subnetMask;
            String[] ips = ip.split("\\.");
            int ipAddr = (Integer.parseInt(ips[0]) << 24) | (Integer.parseInt(ips[1]) << 16) | (Integer.parseInt(ips[2]) << 8) | Integer.parseInt(ips[3]);
            int type = Integer.parseInt(cidr.replaceAll(".*/", ""));
            int mask = 0xFFFFFFFF << (32 - type);
            String cidrIp = cidr.replaceAll("/.*", "");
            String[] cidrIps = cidrIp.split("\\.");
            int cidrIpAddr = (Integer.parseInt(cidrIps[0]) << 24) | (Integer.parseInt(cidrIps[1]) << 16) | (Integer.parseInt(cidrIps[2]) << 8) | Integer.parseInt(cidrIps[3]);
            return (ipAddr & mask) == (cidrIpAddr & mask);
        } catch (Exception ex) {
            throw new IpSubnetMaskException(ip,networkIp + "/" + subnetMask);
        }
    }

    public static boolean checkIp(String param) {
        Pattern pattern = Pattern.compile("^((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]" + "|[*])\\.){3}(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]|[*])$");
        return pattern.matcher(param).matches();
    }

    public static boolean checkMask(int mask) {
        return mask >= 0 && mask <= 32;
    }

}
