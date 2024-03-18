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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.integration.authentication.enums.AuthenticateType;
import org.apache.commons.collections4.MapUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TranslateUtil {
    private static final String APP_ID = "20230329001619570";
    private static final String SECURITY_KEY = "fpdlJ2_xHSuGP9mRYGSP";
    private static final String TRANS_API_HOST = "http://api.fanyi.baidu.com/api/trans/vip/translate";

    public static String getTransResult(String query, String from, String to) {
        JSONObject params = buildParams(query, from, to);
        JSONObject resultJson = HttpRequestUtil.get(TRANS_API_HOST).setQueryString(params).setConnectTimeout(5000).setReadTimeout(10000).setAuthType(AuthenticateType.BUILDIN).sendRequest().getResultJson();
        if (resultJson.containsKey("trans_result")) {
            JSONArray transResult = resultJson.getJSONArray("trans_result");
            if (resultJson.size() > 0) {
                return transResult.getJSONObject(0).getString("dst");
            }
        }
        return null;
    }

    public static JSONArray getBatchTransResult(String query, String from, String to) {
        JSONObject params = buildParams(query, from, to);
        JSONObject resultJson = HttpRequestUtil.get(TRANS_API_HOST).setQueryString(params).setConnectTimeout(5000).setReadTimeout(10000).setAuthType(AuthenticateType.BUILDIN).sendRequest().getResultJson();
        if (MapUtils.isNotEmpty(resultJson) && resultJson.containsKey("trans_result")) {
            return resultJson.getJSONArray("trans_result");
        }
        return null;
    }

    private static JSONObject buildParams(String query, String from, String to) {
        JSONObject params = new JSONObject();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);

        params.put("appid", APP_ID);

        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);

        // 签名
        String src = APP_ID + query + salt + SECURITY_KEY; // 加密前的原文
        params.put("sign", md5(src));

        return params;
    }


    // 首先初始化一个字符数组，用来存放每个16进制字符
    private static final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f'};

    /**
     * 获得一个字符串的MD5值
     *
     * @param input 输入的字符串
     * @return 输入字符串的MD5值
     */
    public static String md5(String input) {
        if (input == null)
            return null;

        try {
            // 拿到一个MD5转换器（如果想要SHA1参数换成”SHA1”）
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // 输入的字符串转换成字节数组
            byte[] inputByteArray = input.getBytes("utf-8");
            // inputByteArray是输入字符串转换得到的字节数组
            messageDigest.update(inputByteArray);
            // 转换并返回结果，也是字节数组，包含16个元素
            byte[] resultByteArray = messageDigest.digest();
            // 字符数组转换成字符串返回
            return byteArrayToHex(resultByteArray);
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String byteArrayToHex(byte[] byteArray) {
        // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
        char[] resultCharArray = new char[byteArray.length * 2];
        // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }

        // 字符数组组合成字符串返回
        return new String(resultCharArray);

    }
}
