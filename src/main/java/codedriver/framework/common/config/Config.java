/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.common.config;

import codedriver.framework.common.RootConfiguration;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executor;

@RootConfiguration
public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    @NacosInjected
    private ConfigService configService;
    private static final String CONFIG_FILE = "config.properties";

    public static final int SCHEDULE_SERVER_ID;
    public static final String RESPONSE_TYPE_JSON = "application/json;charset=UTF-8";
    public static final String RESPONSE_TYPE_HTML = "text/html;charset=UTF-8";
    public static final String RESPONSE_TYPE_TEXT = "text/plain;charset=UTF-8";
    public static final String RC4KEY = "codedriver.key.20200101";

    private static String JWT_SECRET = "techsure#codedriver$secret";
    private static String CODEDRIVER_HOME;
    //    private static Map<String, String> ES_CLUSTERS;
    private static boolean ES_ENABLE;
    private static String DB_HOST;
    private static Integer DB_PORT;
    private static String DB_DRIVER;
    private static String DATA_HOME;// 存储文件路径
    private static int SERVER_HEARTBEAT_RATE;// 心跳频率
    private static int SERVER_HEARTBEAT_THRESHOLD;// 心跳失败上限次数
    private static String HOME_URL;
    private static String USER_EXPIRETIME; // 会话超时时间


    private static String MINIO_URL;
    private static String MINIO_BUCKET;
    private static String MINIO_ACCESSKEY;
    private static String MINIO_SECRETKEY;

    private static String MOBILE_TEST_USER;//移动端测试用户

    private static int NEW_MESSAGE_EXPIRED_DAY;
    private static int HISTORY_MESSAGE_EXPIRED_DAY;

    private static Boolean ENABLE_SUPERADMIN;//是否激活超级管理员，超级管理员用户名是techsure，是虚拟用户，免密登录，拥有管理员权限，可以授权给其他真实用户
    private static Boolean ENABLE_INTERFACE_VERIFY;//是否激活接口参数校验
    private static Boolean ENABLE_NO_SECRET;//是否激活免密登录，用户只校验用户名，不校验密码
    private static Boolean ENABLE_GZIP; //是否激活数据库大字段压缩

    private static String PUBLIC_API_AUTH_USERNAME;//外部访问接口basic认证用户名
    private static String PUBLIC_API_AUTH_PASSWORD;//外部访问接口basic认证密码

    static {
        CODEDRIVER_HOME = System.getenv("CODEDRIVER_HOME");
        if (StringUtils.isBlank(CODEDRIVER_HOME)) {
            CODEDRIVER_HOME = System.getProperty("codedriverhome");
            if (StringUtils.isBlank(CODEDRIVER_HOME)) {
                CODEDRIVER_HOME = "/app";
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
            logger.error("【配置文件初始化失败】请在" + CONFIG_FILE + "中配置schedule.server.id变量");
            System.out.println("【配置文件初始化失败】请在" + CONFIG_FILE + "中配置schedule.server.id变量");
            throw ex;
        }
    }

    public static String CODEDRIVER_HOME() {
        return CODEDRIVER_HOME;
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

    public static String DB_DRIVER() {
        return DB_DRIVER;
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

    public static int SERVER_HEARTBEAT_THRESHOLD() {
        return SERVER_HEARTBEAT_THRESHOLD;
    }

    public static String HOME_URL() {
        return HOME_URL;
    }

    public static int USER_EXPIRETIME() {
        return Integer.parseInt(USER_EXPIRETIME);
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


    public static int NEW_MESSAGE_EXPIRED_DAY() {
        return NEW_MESSAGE_EXPIRED_DAY;
    }

    public static int HISTORY_MESSAGE_EXPIRED_DAY() {
        return HISTORY_MESSAGE_EXPIRED_DAY;
    }

    @PostConstruct
    public void init() {
        try {
            String propertiesString = configService.getConfig("config", "codedriver.framework", 3000);
            loadNacosProperties(propertiesString);
            configService.addListener("config", "codedriver.framework", new Listener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    loadNacosProperties(configInfo);
                }

                @Override
                public Executor getExecutor() {
                    return null;
                }
            });
        } catch (NacosException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static void loadNacosProperties(String configInfo) {
        try {
            Properties prop = new Properties();
            if (StringUtils.isNotBlank(configInfo)) {
                prop.load(new ByteArrayInputStream(configInfo.getBytes()));
            } else {
                // 如果从nacos中读不出配置，则使用本地配置文件配置
                prop.load(new InputStreamReader(Objects.requireNonNull(Config.class.getClassLoader().getResourceAsStream(CONFIG_FILE)), StandardCharsets.UTF_8));
            }
            DATA_HOME = prop.getProperty("data.home", "/app/data");
            SERVER_HEARTBEAT_RATE = Integer.parseInt(prop.getProperty("heartbeat.rate", "1"));
            SERVER_HEARTBEAT_THRESHOLD = Integer.parseInt(prop.getProperty("heartbeat.threshold", "3"));
            HOME_URL = prop.getProperty("home.url");
            JWT_SECRET = prop.getProperty("jwt.secret", "techsure#codedriver$secret");
            USER_EXPIRETIME = prop.getProperty("user.expiretime", "60");
            DB_HOST = prop.getProperty("db.host", "localhost");
            DB_PORT = Integer.parseInt(prop.getProperty("db.port", "3306"));
            DB_DRIVER = prop.getProperty("db.driverClassName", "com.mysql.jdbc.Driver");
            MINIO_URL = prop.getProperty("minio.url");
            MINIO_ACCESSKEY = prop.getProperty("minio.accesskey", "minioadmin");
            MINIO_SECRETKEY = prop.getProperty("minio.secretkey", "minioadmin");
            MINIO_BUCKET = prop.getProperty("minio.bucket", "codedriver");
            MOBILE_TEST_USER = prop.getProperty("mobile.test.user");
            NEW_MESSAGE_EXPIRED_DAY = Integer.parseInt(prop.getProperty("new.message.expired.day", "7"));
            HISTORY_MESSAGE_EXPIRED_DAY = Integer.parseInt(prop.getProperty("history.message.expired.day", "15"));
            ES_ENABLE = Boolean.parseBoolean(prop.getProperty("es.enable", "false"));
            ENABLE_GZIP = Boolean.parseBoolean(prop.getProperty("gzip.enable", "false"));
            PUBLIC_API_AUTH_USERNAME = prop.getProperty("public.api.auth.username", "techsure");
            PUBLIC_API_AUTH_PASSWORD = prop.getProperty("public.api.auth.password", "x15wDEzSbBL6tV1W");
            //ES_CLUSTERS = new HashMap<>();

            /*for (Map.Entry<Object, Object> el : prop.entrySet()) {
                Object k = el.getKey();
                Object v = el.getValue();

                if (!(k instanceof String) || !(v instanceof String)) {
                    continue;
                }

                String key = (String) k;
                String value = (String) v;

                final String keyPrefix = "es.cluster.";
                if (!key.startsWith(keyPrefix)) {
                    continue;
                }
                String clusterName = key.trim().substring(keyPrefix.length());
                if (clusterName.isEmpty()) {
                    continue;
                }
                ES_CLUSTERS.put(clusterName, value);
            }*/

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static String getProperty(String configFile, String keyName, String defaultValue, boolean isRequired) {
        Properties pro = new Properties();
        try (InputStream is = Config.class.getClassLoader().getResourceAsStream(configFile)) {
            pro.load(is);
            String value = pro.getProperty(keyName, defaultValue);
            if (value != null) {
                value = value.trim();
            }
            return value;
        } catch (Exception e) {
            if (isRequired) {
                logger.error(e.getMessage(), e);
            }
        }
        return "";
    }

    public static String getProperty(String configFile, String keyName) {
        return getProperty(configFile, keyName, "", false);
    }

    public static String getProperty(String configFile, String keyName, boolean isRequired) {
        return getProperty(configFile, keyName, "", isRequired);
    }


}
