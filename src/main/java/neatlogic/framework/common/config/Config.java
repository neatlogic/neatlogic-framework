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

package neatlogic.framework.common.config;

import neatlogic.framework.common.RootConfiguration;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executor;

@RootConfiguration
public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    @NacosInjected
    private ConfigService configService;
    private static final String CONFIG_FILE = "config.properties";

    public static final int SCHEDULE_SERVER_ID;
    public static final String SERVER_HOST;
    public static final String RESPONSE_TYPE_JSON = "application/json;charset=UTF-8";

    private static String JWT_SECRET = "techsure#neatlogic$secret";
    private static String NEATLOGIC_HOME;
    //    private static Map<String, String> ES_CLUSTERS;
    private static boolean ES_ENABLE;
    private static String DB_HOST;
    private static Integer DB_PORT;
    private static String DB_URL;
    private static String DATA_HOME;// ??????????????????
    private static int SERVER_HEARTBEAT_RATE;// ????????????
    private static int SERVER_HEARTBEAT_THRESHOLD;// ????????????????????????
    private static int MQ_SUBSCRIBE_RECONNECT_PERIOD;//MQ??????????????????
    private static String HOME_URL;
    private static String USER_EXPIRETIME; // ??????????????????(???)
    private static int LOGIN_CAPTCHA_EXPIRED_TIME; //?????????????????????(???)
    private static int LOGIN_FAILED_TIMES_CAPTCHA; //??????????????????????????????????????????

    private static String MONGO_HOST;
    private static String MONGO_PASSWORD;
    private static String MONGO_USERNAME;
    private static String MONGO_DATABASE;

    private static String JMS_URL;


    private static String MINIO_URL;
    private static String MINIO_BUCKET;
    private static String MINIO_ACCESSKEY;
    private static String MINIO_SECRETKEY;

    private static String MOBILE_TEST_USER;//?????????????????????
    private static Boolean MOBILE_IS_ONLINE;//?????????????????????

    private static int NEW_MESSAGE_EXPIRED_DAY;
    private static int HISTORY_MESSAGE_EXPIRED_DAY;

    private static Boolean ENABLE_SUPERADMIN;//?????????????????????????????????????????????????????????administrator?????????????????????????????????????????????????????????????????????????????????????????????
    private static String SUPERADMIN;//?????????????????????
    private static String SUPERADMIN_PASSWORD;//?????????????????????
    private static Boolean ENABLE_INTERFACE_VERIFY;//??????????????????????????????
    private static Boolean ENABLE_NO_SECRET;//?????????????????????????????????????????????????????????????????????
    private static Boolean ENABLE_GZIP; //????????????????????????????????????

    private static String PUBLIC_API_AUTH_USERNAME;//??????????????????basic???????????????
    private static String PUBLIC_API_AUTH_PASSWORD;//??????????????????basic????????????

    private static String RUNNER_CONTEXT;//runner??????

    //wechat
    private static String WECHAT_CORP_ID;//??????Id?????????"????????????"???????????????
    private static String WECHAT_APP_SECRET;//??????????????????????????????"????????????"???????????????????????????
    private static String WECHAT_APP_AGENT_ID;//????????????ID (??????"????????????"???????????????????????????
    private static String WECHAT_ACCESS_TOKEN_URL;//??????????????????token??????
    private static String WECHAT_USERINFO_URL;//??????????????????user??????

    private static String LICENSE_PK; //license??????

    private static String LICENSE; //license

    private static String DIRECT_URL;//????????????????????????url

    private static String SSO_TICKET_KEY;//sso?????????ticket key

    static {
        NEATLOGIC_HOME = System.getenv("NEATLOGIC_HOME");
        if (StringUtils.isBlank(NEATLOGIC_HOME)) {
            NEATLOGIC_HOME = System.getProperty("neatlogichome");
            if (StringUtils.isBlank(NEATLOGIC_HOME)) {
                NEATLOGIC_HOME = "/app";
            }
        }

        if (StringUtils.isNotBlank(System.getProperty("enableNoSecret"))) {
            try {
                ENABLE_NO_SECRET = Boolean.valueOf(System.getProperty("enableNoSecret"));
            } catch (Exception ex) {
                ENABLE_NO_SECRET = false;
            }
        } else {
            ENABLE_NO_SECRET = false;
        }

        if (StringUtils.isNotBlank(System.getProperty("enableInterfaceVerify"))) {
            try {
                ENABLE_INTERFACE_VERIFY = Boolean.valueOf(System.getProperty("enableInterfaceVerify"));
            } catch (Exception ex) {
                ENABLE_INTERFACE_VERIFY = false;
            }
        } else {
            ENABLE_INTERFACE_VERIFY = false;
        }

        if (StringUtils.isNotBlank(System.getProperty("enableSuperAdmin"))) {
            try {
                ENABLE_SUPERADMIN = Boolean.valueOf(System.getProperty("enableSuperAdmin"));
            } catch (Exception ex) {
                ENABLE_SUPERADMIN = false;
            }
        } else {
            ENABLE_SUPERADMIN = false;
        }

        try {
            String sid = getProperty(CONFIG_FILE, "schedule.server.id", "1", true);
            if (StringUtils.isBlank(sid)) {
                sid = "1";
            }
            SCHEDULE_SERVER_ID = Integer.parseInt(sid);
        } catch (Exception ex) {
            logger.error("???????????????????????????????????????" + CONFIG_FILE + "?????????schedule.server.id??????");
            System.out.println("???????????????????????????????????????" + CONFIG_FILE + "?????????schedule.server.id??????");
            throw ex;
        }

        try {
            SERVER_HOST = getProperty(CONFIG_FILE, "server.host", true);
        } catch (Exception ex) {
            logger.error("???????????????????????????????????????" + CONFIG_FILE + "?????????server.host??????");
            System.out.println("???????????????????????????????????????" + CONFIG_FILE + "?????????server.host??????");
            throw ex;
        }
    }

    public static String NEATLOGIC_HOME() {
        return NEATLOGIC_HOME;
    }

    public static String JWT_SECRET() {
        return JWT_SECRET;
    }

    /*public static Map<String, String> ES_CLUSTERS() {
        return ES_CLUSTERS;
    }*/

    public static boolean ES_ENABLE() {
        return ES_ENABLE;
    }

    public static String DB_HOST() {
        return DB_HOST;
    }

    public static Integer DB_PORT() {
        return DB_PORT;
    }

    public static String DB_URL() {
        return DB_URL;
    }

    public static String MONGO_HOST() {
        return MONGO_HOST;
    }

    public static String MONGO_PASSWORD() {
        return MONGO_PASSWORD;
    }

    public static String MONGO_DATABASE() {
        return MONGO_DATABASE;
    }

    public static String MONGO_USERNAME() {
        return MONGO_USERNAME;
    }

    public static String JMS_URL() {
        return JMS_URL;
    }

    public static String DATA_HOME() {
        if (!DATA_HOME.endsWith(File.separator)) {
            DATA_HOME += File.separator;
        }
        return DATA_HOME;
    }

    public static int SERVER_HEARTBEAT_RATE() {
        return SERVER_HEARTBEAT_RATE;
    }

    public static int MQ_SUBSCRIBE_RECONNECT_PERIOD() {
        return MQ_SUBSCRIBE_RECONNECT_PERIOD;
    }

    public static int SERVER_HEARTBEAT_THRESHOLD() {
        return SERVER_HEARTBEAT_THRESHOLD;
    }

    public static String HOME_URL() {
        return HOME_URL;
    }

    public static int USER_EXPIRETIME() {
        return Integer.parseInt(USER_EXPIRETIME);
    }

    public static int LOGIN_CAPTCHA_EXPIRED_TIME() {
        return LOGIN_CAPTCHA_EXPIRED_TIME;
    }

    public static int LOGIN_FAILED_TIMES_CAPTCHA() {
        return LOGIN_FAILED_TIMES_CAPTCHA;
    }

    public static String MINIO_URL() {
        return MINIO_URL;
    }

    public static String MINIO_ACCESSKEY() {
        return MINIO_ACCESSKEY;
    }

    public static String MINIO_SECRETKEY() {
        return MINIO_SECRETKEY;
    }

    public static String MINIO_BUCKET() {
        return MINIO_BUCKET;
    }

    public static String MOBILE_TEST_USER() {
        return MOBILE_TEST_USER;
    }

    public static Boolean MOBILE_IS_ONLINE() {
        return MOBILE_IS_ONLINE;
    }

    public static boolean ENABLE_INTERFACE_VERIFY() {
        return ENABLE_INTERFACE_VERIFY;
    }

    public static boolean ENABLE_SUPERADMIN() {
        return ENABLE_SUPERADMIN;
    }

    public static boolean ENABLE_NO_SECRET() {
        return ENABLE_NO_SECRET;
    }

    public static boolean ENABLE_GZIP() {
        return ENABLE_GZIP;
    }

    public static String PUBLIC_API_AUTH_USERNAME() {
        return PUBLIC_API_AUTH_USERNAME;
    }

    public static String PUBLIC_API_AUTH_PASSWORD() {
        return PUBLIC_API_AUTH_PASSWORD;
    }


    public static String RUNNER_CONTEXT() {
        return RUNNER_CONTEXT;
    }

    public static int NEW_MESSAGE_EXPIRED_DAY() {
        return NEW_MESSAGE_EXPIRED_DAY;
    }

    public static int HISTORY_MESSAGE_EXPIRED_DAY() {
        return HISTORY_MESSAGE_EXPIRED_DAY;
    }

    public static String WECHAT_ACCESS_TOKEN_URL() {
        return WECHAT_ACCESS_TOKEN_URL;
    }

    public static String WECHAT_USERINFO_URL() {
        return WECHAT_USERINFO_URL;
    }

    public static String WECHAT_CORP_ID() {
        return WECHAT_CORP_ID;
    }

    public static String WECHAT_APP_SECRET() {
        return WECHAT_APP_SECRET;
    }

    public static String WECHAT_APP_AGENT_ID() {
        return WECHAT_APP_AGENT_ID;
    }

    public static String LICENSE_PK() {
        return LICENSE_PK;
    }

    public static String LICENSE() {
        return LICENSE;
    }

    public static String SUPERADMIN() {
        return SUPERADMIN;
    }

    public static String SUPERADMIN_PASSWORD() {
        return SUPERADMIN_PASSWORD;
    }

    public static String DIRECT_URL() {
        return DIRECT_URL;
    }

    public static String SSO_TICKET_KEY() {
        return SSO_TICKET_KEY;
    }


    @PostConstruct
    public void init() {
        try {
            String propertiesString = configService.getConfig("config", "neatlogic.framework", 3000);
            loadNacosProperties(propertiesString);
            if (StringUtils.isNotBlank(propertiesString)) {
                configService.addListener("config", "neatlogic.framework", new Listener() {
                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        loadNacosProperties(configInfo);
                    }

                    @Override
                    public Executor getExecutor() {
                        return null;
                    }
                });
            }
        } catch (NacosException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static void loadNacosProperties(String configInfo) {
        try {
            Properties prop = new Properties();
            if (StringUtils.isNotBlank(configInfo)) {
                prop.load(new InputStreamReader(new ByteArrayInputStream(configInfo.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
            } else {
                // ?????????nacos??????????????????????????????????????????????????????
                prop.load(new InputStreamReader(Objects.requireNonNull(Config.class.getClassLoader().getResourceAsStream(CONFIG_FILE)), StandardCharsets.UTF_8));
            }
            DATA_HOME = prop.getProperty("data.home", "/app/data");
            SERVER_HEARTBEAT_RATE = Integer.parseInt(prop.getProperty("heartbeat.rate", "1"));
            SERVER_HEARTBEAT_THRESHOLD = Integer.parseInt(prop.getProperty("heartbeat.threshold", "3"));
            MQ_SUBSCRIBE_RECONNECT_PERIOD = Integer.parseInt(prop.getProperty("mq.subscribe.reconnect.period", "5"));
            HOME_URL = prop.getProperty("home.url");
            JWT_SECRET = prop.getProperty("jwt.secret", "techsure#neatlogic$secret");
            USER_EXPIRETIME = prop.getProperty("user.expiretime", "60");
            LOGIN_CAPTCHA_EXPIRED_TIME = Integer.parseInt(prop.getProperty("login.captcha.expired.time", "60"));
            LOGIN_FAILED_TIMES_CAPTCHA = Integer.parseInt(prop.getProperty("login.failed.times.captcha", "3"));
            DB_URL = prop.getProperty("db.url");
            DB_HOST = prop.getProperty("db.host", "localhost");
            DB_PORT = Integer.parseInt(prop.getProperty("db.port", "3306"));
            MONGO_HOST = prop.getProperty("mongo.host", "localhost:27017");
            MONGO_USERNAME = prop.getProperty("mongo.username", "root");
            MONGO_PASSWORD = prop.getProperty("mongo.password", "root");
            MONGO_DATABASE = prop.getProperty("mongo.database", "neatlogic");

            JMS_URL = prop.getProperty("jms.url", "tcp://localhost:8161");

            MINIO_URL = prop.getProperty("minio.url");
            MINIO_ACCESSKEY = prop.getProperty("minio.accesskey", "minioadmin");
            MINIO_SECRETKEY = prop.getProperty("minio.secretkey", "minioadmin");
            MINIO_BUCKET = prop.getProperty("minio.bucket", "neatlogic");
            MOBILE_TEST_USER = prop.getProperty("mobile.test.user");
            MOBILE_IS_ONLINE = Boolean.parseBoolean(prop.getProperty("mobile.is.online", "false"));
            NEW_MESSAGE_EXPIRED_DAY = Integer.parseInt(prop.getProperty("new.message.expired.day", "7"));
            HISTORY_MESSAGE_EXPIRED_DAY = Integer.parseInt(prop.getProperty("history.message.expired.day", "15"));
            ES_ENABLE = Boolean.parseBoolean(prop.getProperty("es.enable", "false"));
            ENABLE_GZIP = Boolean.parseBoolean(prop.getProperty("gzip.enable", "false"));
            PUBLIC_API_AUTH_USERNAME = prop.getProperty("public.api.auth.username", "techsure");
            PUBLIC_API_AUTH_PASSWORD = prop.getProperty("public.api.auth.password", "x15wDEzSbBL6tV1W");
            RUNNER_CONTEXT = prop.getProperty("runner.context", "/autoexecrunner");

            WECHAT_ACCESS_TOKEN_URL = prop.getProperty("wechat.access.token.url", "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=CorpID&corpsecret=SECRET");
            WECHAT_USERINFO_URL = prop.getProperty("wechat.userinfo.url", "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=ACCESS_TOKEN&code=CODE&agentid=AGENTID");
            WECHAT_CORP_ID = prop.getProperty("wechat.corp.id");
            WECHAT_APP_SECRET = prop.getProperty("wechat.app.secret");
            WECHAT_APP_AGENT_ID = prop.getProperty("wechat.app.agent.id");

            LICENSE_PK = prop.getProperty("license.pk");
            LICENSE = prop.getProperty("license");
            SUPERADMIN = prop.getProperty("superadmin", "administrator");
            SUPERADMIN_PASSWORD = prop.getProperty("superadmin.password", "RC4:68b72d0a4d801e4148b8a50419f0dc3e0f04");

            DIRECT_URL = prop.getProperty("direct.url");
            SSO_TICKET_KEY = prop.getProperty("sso.ticket.key");

            //??????????????????
            Reflections reflections = new Reflections("neatlogic");
            Set<Class<? extends IConfigListener>> listeners = reflections.getSubTypesOf(IConfigListener.class);
            for (Class<? extends IConfigListener> c : listeners) {
                IConfigListener handler;
                try {
                    handler = c.newInstance();
                    handler.loadConfig(prop);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static String getProperty(String configFile, String keyName, String defaultValue, boolean isRequired) {
        String value = null;
        Properties pro = new Properties();
        try (InputStream is = Config.class.getClassLoader().getResourceAsStream(configFile)) {
            pro.load(is);
            value = pro.getProperty(keyName, defaultValue);
            if (value != null) {
                value = value.trim();
            }
        } catch (Exception e) {
           // logger.error(e.getMessage(), e);
        }
        if (value == null && isRequired) {
            throw new RuntimeException();
        }
        return value;
    }

    public static String getProperty(String configFile, String keyName) {
        return getProperty(configFile, keyName, "", false);
    }

    public static String getProperty(String configFile, String keyName, boolean isRequired) {
        return getProperty(configFile, keyName, null, isRequired);
    }

}
