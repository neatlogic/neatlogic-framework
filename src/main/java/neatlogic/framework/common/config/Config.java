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

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import neatlogic.framework.common.RootConfiguration;
import neatlogic.framework.util.I18nUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executor;

@DependsOn({"i18nManager", "messageSourceAccessor"})
@RootConfiguration
public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    @NacosInjected
    private ConfigService configService;
    private static final String CONFIG_FILE = "config.properties";
    private static final String SERVER_ID_FILE = "serverid.conf";

    public static int SCHEDULE_SERVER_ID;
    //    public static String SERVER_HOST;
    public static final String RESPONSE_TYPE_JSON = "application/json;charset=UTF-8";

    private static String JWT_SECRET = "neatlogic#neatlogic$secret";
    private static String NEATLOGIC_HOME;
    //    private static Map<String, String> ES_CLUSTERS;
    private static boolean ES_ENABLE;
    private static String DB_HOST;
    private static Integer DB_PORT;
    private static String DB_URL;
    private static String DB_TRANSACTION_TIMEOUT;// 事务超时时间
    private static String DATA_HOME;// 存储文件路径
    private static int SERVER_HEARTBEAT_RATE;// 心跳频率
    private static int SERVER_HEARTBEAT_THRESHOLD;// 心跳失败上限次数
    private static int MQ_SUBSCRIBE_RECONNECT_PERIOD;//MQ连接重试间隔
    private static String HOME_URL;// 前端服务器地址，例如：http://192.168.0.10:8099
    private static String MASTER_HOME_URL;// 前端租户管理服务器地址，例如：http://192.168.0.10:9099
    private static String BACK_END_URL;// 后端服务器地址，例如：http://192.168.0.25:8282/neatlogic
    private static String USER_EXPIRETIME; // 会话超时时间(分)
    private static int LOGIN_CAPTCHA_EXPIRED_TIME; //验证码超时时间(秒)
    private static int LOGIN_FAILED_TIMES_CAPTCHA; //设置需要验证码的登录错误次数

    private static String MONGO_HOST;
    private static String MONGO_PASSWORD;
    private static String MONGO_USERNAME;
    private static String MONGO_DATABASE;

    private static String JMS_URL;


    private static String MOBILE_TEST_USER;//移动端测试用户
    private static Boolean MOBILE_IS_ONLINE;//是否启动移动端

    private static int NEW_MESSAGE_EXPIRED_DAY;
    private static int HISTORY_MESSAGE_EXPIRED_DAY;

    private static Boolean ENABLE_MAINTENANCE;//是否激活运维用户，是虚拟用户，用于只有在系统不存在用户，出厂时使用，可以新增用户、角色、分组和授权
    private static String MAINTENANCE;//运维用户，默认是 administrator
    private static String MAINTENANCE_PASSWORD;//运维用户密码
    private static Boolean ENABLE_INTERFACE_VERIFY;//是否激活接口参数校验
    private static Boolean ENABLE_NO_SECRET;//是否激活免密登录，用户只校验用户名，不校验密码
    private static Boolean ENABLE_GZIP; //是否激活数据库大字段压缩

    private static String PUBLIC_API_AUTH_USERNAME;//外部访问接口basic认证用户名
    private static String PUBLIC_API_AUTH_PASSWORD;//外部访问接口basic认证密码

    private static String RUNNER_CONTEXT;//runner地址

    //wechat
//    private static String WECHAT_CORP_ID;//企业Id（前往"我的企业"菜单获取）
//    private static String WECHAT_APP_SECRET;//应用的凭证密钥（前往"应用管理"找到目标应用获取）
//    private static String WECHAT_APP_AGENT_ID;//企业应用ID (前往"应用管理"找到目标应用获取）
    private static String WECHAT_ACCESS_TOKEN_URL;//获取企业微信token链接
    private static String WECHAT_USERINFO_URL;//获取企业微信user链接

    private static String WECHAT_SEND_MESSAGE_URL; //推送企业微信通知链接

    private static final String LICENSE_PK = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDHfhFEod4K3VlDdF0zGFLeavGyokUt/Njczl/4yAwpOvTUNW0x3C4Uc49NjP3Vr8KYYbsXk7DKv+8hoENGfltSzepmPpX4Mu+iclUqyaLbLHoX+rB7SagjiekuG3B1/ADPLsuG6VFkC00yEFkMUmZhU5IVbTTTKEpCtH8bJxa1aQIDAQAB"; //license公钥

    private static String LICENSE; //license

    private static String DIRECT_URL;//登录失败默认跳转url

    private static String SSO_TICKET_KEY;//sso免登录，url中获取的ticket的参数名

    private static String LOGIN_AUTH_TYPE;//系统登录方式,默认是default，DB登录
    private static String LOGIN_AUTH_PASSWORD_ENCRYPT;//系统登录密码加密策略，默认是md5

    private static String LDAP_SERVER_URL;//ldap服务url

    private static String LDAP_USER_DN;//ldap userDn格式

    private static String AUTOEXEC_TOKEN;// autoexec用户的token

    private static String FILE_HANDLER;//文件处理器

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

        if (StringUtils.isNotBlank(System.getProperty("enableMaintenance"))) {
            try {
                ENABLE_MAINTENANCE = Boolean.valueOf(System.getProperty("enableMaintenance"));
            } catch (Exception ex) {
                ENABLE_MAINTENANCE = false;
            }
        } else {
            ENABLE_MAINTENANCE = false;
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

    public static String DB_TRANSACTION_TIMEOUT() {
        return DB_TRANSACTION_TIMEOUT;
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

    public static String FILE_HANDLER() {
        return FILE_HANDLER;
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

    public static String MASTER_HOME_URL() {
        if (StringUtils.isNotBlank(MASTER_HOME_URL) && !MASTER_HOME_URL.endsWith("/")) {
            return MASTER_HOME_URL + "/";
        }
        return MASTER_HOME_URL;
    }

    public static String BACK_END_URL() {
        return BACK_END_URL;
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


    public static String MOBILE_TEST_USER() {
        return MOBILE_TEST_USER;
    }

    public static Boolean MOBILE_IS_ONLINE() {
        return MOBILE_IS_ONLINE;
    }

    public static boolean ENABLE_INTERFACE_VERIFY() {
        return ENABLE_INTERFACE_VERIFY;
    }

    public static boolean ENABLE_MAINTENANCE() {
        return ENABLE_MAINTENANCE;
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

//    public static String WECHAT_CORP_ID() {
//        return WECHAT_CORP_ID;
//    }
//
//    public static String WECHAT_APP_SECRET() {
//        return WECHAT_APP_SECRET;
//    }
//
//    public static String WECHAT_APP_AGENT_ID() {
//        return WECHAT_APP_AGENT_ID;
//    }

    public static String WECHAT_SEND_MESSAGE_URL() {
        return WECHAT_SEND_MESSAGE_URL;
    }

    public static String LICENSE_PK() {
        return LICENSE_PK;
    }

    public static String LICENSE() {
        return LICENSE;
    }

    public static String MAINTENANCE() {
        return MAINTENANCE;
    }

    public static String MAINTENANCE_PASSWORD() {
        return MAINTENANCE_PASSWORD;
    }

    public static String DIRECT_URL() {
        return DIRECT_URL;
    }

    public static String SSO_TICKET_KEY() {
        return SSO_TICKET_KEY;
    }

    public static String LOGIN_AUTH_TYPE() {
        return LOGIN_AUTH_TYPE;
    }

    public static String LOGIN_AUTH_PASSWORD_ENCRYPT() {
        return LOGIN_AUTH_PASSWORD_ENCRYPT;
    }

    public static String LDAP_SERVER_URL() {
        return LDAP_SERVER_URL;
    }

    public static String LDAP_USER_DN() {
        return LDAP_USER_DN;
    }

    public static String AUTOEXEC_TOKEN() {
        return AUTOEXEC_TOKEN;
    }

    public static Properties properties = new Properties();

    private void initConfigFile() {
        try {
            StringBuilder sid = new StringBuilder(StringUtils.EMPTY);
            BufferedReader br;
            try (InputStream is = Config.class.getClassLoader().getResourceAsStream(SERVER_ID_FILE);) {
                if (is != null) {
                    try (InputStreamReader in = new InputStreamReader(is, StandardCharsets.UTF_8);) {
                        br = new BufferedReader(in);
                        String inLine = "";
                        while ((inLine = br.readLine()) != null) {
                            sid.append(inLine);
                        }
                    }
                } else {
                    String classpath = System.getenv("CLASSPATH");
                    String[] split = classpath.split(":");
                    System.out.println("配置文件目录：" + split[0]);
                    File file = new File(split[0] + File.separator + SERVER_ID_FILE);
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    file.createNewFile();
                    try (FileWriter fw = new FileWriter(file)) {
                        Random random = new Random();
                        int i = random.nextInt();
                        if (i < 0) {
                            i = Math.abs(i);
                        }
                        sid.append(i);
                        fw.write(sid.toString());
                        fw.flush();
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            } catch (Exception e) {
                // logger.error(e.getMessage(), e);
            }
            SCHEDULE_SERVER_ID = Integer.parseInt(sid.toString());
        } catch (Exception ex) {
            logger.error("【缺少服务唯一标识】请在classpath所在目录创建文件：" + SERVER_ID_FILE + "，并填入一个正整数，正常启动后不要随意修改此数字，如果采用多活方式部署，唯一标识不能重复。");
            System.out.println("【缺少服务唯一标识】请在classpath所在目录创建文件：" + SERVER_ID_FILE + "，并填入一个正整数，正常启动后不要随意修改此数字，如果采用多活方式部署，唯一标识不能重复。");
            throw ex;
        }

//        try {
//            SERVER_HOST = getProperty(CONFIG_FILE, "server.host", true);
//        } catch (Exception ex) {
//            logger.error("【配置文件初始化失败】请在" + CONFIG_FILE + "中配置server.host变量");
//            System.out.println("【配置文件初始化失败】请在" + CONFIG_FILE + "中配置server.host变量");
//            throw ex;
//        }
//
//        Pattern pattern = RegexUtils.getPattern(RegexUtils.SERVER_HOST);
//        Matcher matcher = pattern.matcher(SERVER_HOST);
//        if (!matcher.matches()) {
//            throw new RuntimeException("【配置文件初始化失败】，在" + CONFIG_FILE + "中变量server.host=" + SERVER_HOST + "的值不符格式要求，格式为“(http|https)://IP地址:端口”");
//        }
    }

    @PostConstruct
    public void init() {
        try {
            initConfigFile();
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
                System.out.println("⚡" + I18nUtils.getStaticMessage("common.startloadconfig", "Nacos"));
            } else {
                // 如果从nacos中读不出配置，则使用本地配置文件配置
                prop.load(new InputStreamReader(Objects.requireNonNull(Config.class.getClassLoader().getResourceAsStream(CONFIG_FILE)), StandardCharsets.UTF_8));
                System.out.println("⚡" + I18nUtils.getStaticMessage("common.startloadconfig", "config.properties"));
            }
            DATA_HOME = prop.getProperty("data.home", "/app/data");
            SERVER_HEARTBEAT_RATE = Integer.parseInt(prop.getProperty("heartbeat.rate", "1"));
            SERVER_HEARTBEAT_THRESHOLD = Integer.parseInt(prop.getProperty("heartbeat.threshold", "3"));
            MQ_SUBSCRIBE_RECONNECT_PERIOD = Integer.parseInt(prop.getProperty("mq.subscribe.reconnect.period", "5"));
            HOME_URL = prop.getProperty("home.url");
            MASTER_HOME_URL = prop.getProperty("master.home.url");
            BACK_END_URL = prop.getProperty("back.end.url");
            JWT_SECRET = prop.getProperty("jwt.secret", "neatlogic#neatlogic$secret");
            USER_EXPIRETIME = prop.getProperty("user.expiretime", "60");
            LOGIN_CAPTCHA_EXPIRED_TIME = Integer.parseInt(prop.getProperty("login.captcha.expired.time", "60"));
            LOGIN_FAILED_TIMES_CAPTCHA = Integer.parseInt(prop.getProperty("login.failed.times.captcha", "3"));
            DB_TRANSACTION_TIMEOUT = prop.getProperty("db.transaction.timeout");
            DB_URL = prop.getProperty("db.url");
            DB_HOST = prop.getProperty("db.host", "localhost");
            DB_PORT = Integer.parseInt(prop.getProperty("db.port", "3306"));
            MONGO_HOST = prop.getProperty("mongo.host", "localhost:27017");
            MONGO_USERNAME = prop.getProperty("mongo.username", "root");
            MONGO_PASSWORD = prop.getProperty("mongo.password", "root");
            MONGO_DATABASE = prop.getProperty("mongo.database", "neatlogic");

            JMS_URL = prop.getProperty("jms.url", "tcp://localhost:61616");
            FILE_HANDLER = prop.getProperty("file.handler", "FILE");

            MOBILE_TEST_USER = prop.getProperty("mobile.test.user");
            MOBILE_IS_ONLINE = Boolean.parseBoolean(prop.getProperty("mobile.is.online", "false"));
            NEW_MESSAGE_EXPIRED_DAY = Integer.parseInt(prop.getProperty("new.message.expired.day", "7"));
            HISTORY_MESSAGE_EXPIRED_DAY = Integer.parseInt(prop.getProperty("history.message.expired.day", "15"));
            ES_ENABLE = Boolean.parseBoolean(prop.getProperty("es.enable", "false"));
            ENABLE_GZIP = Boolean.parseBoolean(prop.getProperty("gzip.enable", "false"));
            PUBLIC_API_AUTH_USERNAME = prop.getProperty("public.api.auth.username", "neatlogic");
            PUBLIC_API_AUTH_PASSWORD = prop.getProperty("public.api.auth.password", "x15wDEzSbBL6tV1W");
            RUNNER_CONTEXT = prop.getProperty("runner.context", "/autoexecrunner");

            WECHAT_ACCESS_TOKEN_URL = prop.getProperty("wechat.access.token.url", "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=CorpID&corpsecret=SECRET");
            WECHAT_USERINFO_URL = prop.getProperty("wechat.userinfo.url", "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=ACCESS_TOKEN&code=CODE&agentid=AGENTID");
            WECHAT_SEND_MESSAGE_URL = prop.getProperty("wechat.send.message.url", "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=ACCESS_TOKEN");
//            WECHAT_CORP_ID = prop.getProperty("wechat.corp.id");
//            WECHAT_APP_SECRET = prop.getProperty("wechat.app.secret");
//            WECHAT_APP_AGENT_ID = prop.getProperty("wechat.app.agent.id");

            //LICENSE_PK = prop.getProperty("license.pk");
            LICENSE = prop.getProperty("license");
            MAINTENANCE = prop.getProperty("maintenance", "administrator");
            MAINTENANCE_PASSWORD = prop.getProperty("maintenance.password", "RC4:68b72d0a4d801e4148b8a50419f0dc3e0f04");

            DIRECT_URL = prop.getProperty("direct.url");
            SSO_TICKET_KEY = prop.getProperty("sso.ticket.key");

            LOGIN_AUTH_TYPE = prop.getProperty("login.auth.type");
            LOGIN_AUTH_PASSWORD_ENCRYPT = prop.getProperty("login.auth.password.encrypt", "md5");

            LDAP_SERVER_URL = prop.getProperty("ldap.server.url", "");
            LDAP_USER_DN = prop.getProperty("ldap.user.dn", "");

            AUTOEXEC_TOKEN = prop.getProperty("autoexec.token", "499922b4317c251c2ce525f7b83e3d94");

            //处理其他配置
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
            properties = prop;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static Properties getProperties() {
        return properties;
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
            throw new RuntimeException(String.format("%s is not exist", keyName));
        }
        return value;
    }

    public static String getConfigProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static String getConfigProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getProperty(String configFile, String keyName) {
        return getProperty(configFile, keyName, "", false);
    }

    public static String getProperty(String configFile, String keyName, boolean isRequired) {
        return getProperty(configFile, keyName, null, isRequired);
    }

}
