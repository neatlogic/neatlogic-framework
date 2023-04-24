/*
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. 
 */

package neatlogic.framework.util;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.exception.wechat.WechatGetUserIdFailedException;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WechatUtil {
    private static final Logger logger = LoggerFactory.getLogger(WechatUtil.class);
    private static AccessToken TOKEN = null;

    /**
     * 获取access_token
     *
     * @param corpID 企业Id（前往"我的企业"菜单获取）
     * @param secret 应用的凭证密钥（前往"应用管理"找到目标应用获取）
     * @return token
     */
    public static AccessToken getAccessToken(String corpID, String secret) {
        //优先使用缓存的token
        long ctime = System.currentTimeMillis();
        if (TOKEN != null && ctime < (TOKEN.getCtime() + TOKEN.getExpiresIn() * 1000L)) {
            logger.info("---> re-use token in cache : " + TOKEN.toString());
            return TOKEN;
        }
        AccessToken accessToken = null;
        String requestUrl = Config.WECHAT_ACCESS_TOKEN_URL().replace("CorpID", corpID).replace("SECRET", secret);
        JSONObject jsonObject = HttpRequestUtil.get(requestUrl).setConnectTimeout(10000).sendRequest().getResultJson();
        // 如果请求成功
        if (MapUtils.isNotEmpty(jsonObject)) {
            if (jsonObject.containsKey("access_token")) {
                accessToken = new AccessToken();
                accessToken.setToken(jsonObject.getString("access_token"));
                accessToken.setExpiresIn(jsonObject.getInteger("expires_in"));
                accessToken.setCtime(ctime);
                TOKEN = accessToken;
                logger.info("get token:::" + jsonObject.toJSONString());
            } else {
                // 获取token失败
                throw new RuntimeException(String.format("get accessToken return is empty, errcode:{%s} errmsg:{%s}", jsonObject.getInteger("errcode"), jsonObject.getString("errmsg")));
            }
        } else {
            logger.error(String.format("HttpRequestUtil get accessToken failed ,url:'%s' ", requestUrl));
            throw new RuntimeException("HttpRequestUtil get accessToken failed");
        }
        return accessToken;
    }

    public static AccessToken getAccessToken() {
        String corpID = Config.WECHAT_CORP_ID();
        String appSecret = Config.WECHAT_APP_SECRET();
        return getAccessToken(corpID , appSecret);
    }

    /**
     * 根据code获取成员信息
     *
     * @param access_token 调用接口凭证
     * @param code         通过员工授权获取到的code，每次员工授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期 (企业微信服务器请求时会携带code)
     * @param agentId      企业应用ID (前往"应用管理"找到目标应用获取）
     */
    public static String getUserID(String access_token, String code, String agentId) {
        String UserId = "";
        String url = Config.WECHAT_USERINFO_URL().replace("ACCESS_TOKEN", access_token);
        url = url.replace("CODE", code);
        url = url.replace("AGENTID", agentId);
        JSONObject jsonobject = HttpRequestUtil.get(url).setConnectTimeout(10000).sendRequest().getResultJson();
        if (MapUtils.isNotEmpty(jsonobject)) {
            UserId = jsonobject.getString("UserId");
            if (StringUtils.isNotBlank(UserId)) {
                logger.info("获取信息成功，o(∩_∩)o ————UserID:" + UserId);
            } else {
                int errcode = jsonobject.getInteger("errcode");
                String errmsg = jsonobject.getString("errmsg");
                logger.error("url：" + url);
                logger.error("错误码：" + errcode + "————" + "错误信息：" + errmsg);
                throw new WechatGetUserIdFailedException(String.format("wechat getUserId api return userId is blank! %s---%s", errcode, errmsg));
            }
        } else {
            throw new WechatGetUserIdFailedException("wechat getUserId api return empty!");
        }
        return UserId;
    }

    public static JSONObject httpRequest(String request, String RequestMethod, String output) {
        JSONObject jsonObject = null;
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod(RequestMethod);
            if (output != null) {
                OutputStream out = connection.getOutputStream();
                out.write(output.getBytes("UTF-8"));
                out.close();
            }
            InputStream input = connection.getInputStream();
            InputStreamReader inputReader = new InputStreamReader(input, "UTF-8");
            BufferedReader reader = new BufferedReader(inputReader);
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            reader.close();
            inputReader.close();
            input.close();
            input = null;
            connection.disconnect();
            jsonObject = JSONObject.parseObject(buffer.toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return jsonObject;
    }

    /***
     * textcard 卡片消息格式 详细参数可参数：https://developer.work.weixin.qq.com/document/path/90250
     * @param toUser 发送用户
     * @param title  卡片标题
     * @param content 卡片内容
     * @return
     */
    public static JSONObject getTextCardMsg(String toUser , String title , String content ){
        String forwardURL = "";
        content = content.trim();
        //插件个性化跳转url，存放模板内插件内部处理
        if(content.indexOf("@link:") > -1 ){
            forwardURL = content.substring(content.lastIndexOf("@link:")+6 , content.length());
            String corpID = Config.WECHAT_CORP_ID();
            forwardURL = forwardURL.replace("CorpID", corpID);
            content = content.substring(0 ,content.lastIndexOf("@link:")-1);
        }
        JSONObject data = new JSONObject();
        data.put("touser",toUser);//消息接收者，多个接收者用‘|’分隔
        data.put("msgtype","textcard"); //卡片消息类型，此时固定为：textcard
        JSONObject textCard= new JSONObject();
        textCard.put("title",title);
        textCard.put("description",content);
        textCard.put("url", forwardURL); //必须存在
        data.put("textcard" , textCard);
        data.put("safe","0"); //是否是保密消息，0表示否，1表示是，默认0
        return data;
    }

    /***
     * text文本消息格式 详细参数可参数：https://developer.work.weixin.qq.com/document/path/90250
     * @param toUser 发送用户
     * @param title  卡片标题
     * @param content 卡片内容
     * @return
     */
    public static JSONObject getTextMsg(String toUser , String title , String content ){
        JSONObject data = new JSONObject();
        data.put("touser",toUser);//消息接收者，多个接收者用‘|’分隔
        data.put("msgtype","text"); //消息类型，此时固定为：text
        JSONObject textObj= new JSONObject();
        textObj.put("content",content);
        data.put("text" , textObj);
        data.put("safe","0"); //是否是保密消息，0表示否，1表示是，默认0
        return data;
    }

    /***
     * 发送消息
     * @param access_token
     * @param data
     * @return
     */
    public static int sendMessage(String access_token, JSONObject data) {
        int result = 0;
        String messageUrl = Config.WECHAT_SEND_MESSAGE_URL();
        data.put("agentid",  Config.WECHAT_APP_AGENT_ID());
        messageUrl = messageUrl.replace("ACCESS_TOKEN", access_token);

        JSONObject jsonobject = httpRequest(messageUrl, "POST", data.toString());
        if (null != jsonobject) {
            result = jsonobject.getIntValue("errcode");
            if (0 != result) {
                logger.error("wechat send message，errcode:{} errmsg:{}", result, jsonobject.getString("errmsg"));
                throw new RuntimeException("wechat send message，errcode:{} errmsg:{}"+ result +",msg:"+jsonobject.getString("errmsg"));
            }
        }
        return result;
    }

    public static class AccessToken {
        // 获取到的凭证
        private String token;
        // 凭证有效时间，单位：秒
        private int expiresIn;
        private long ctime;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public int getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(int expiresIn) {
            this.expiresIn = expiresIn;
        }

        public long getCtime() {
            return ctime;
        }

        public void setCtime(long ctime) {
            this.ctime = ctime;
        }

        @Override
        public String toString() {
            return "AccessToken{" +
                    "token='" + token + '\'' +
                    ", expiresIn=" + expiresIn +
                    ", ctime=" + ctime +
                    '}';
        }
    }
}
