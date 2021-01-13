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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executor;

@RootConfiguration
public class Config {
    private static Logger logger = LoggerFactory.getLogger(Config.class);
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
    private static Map<String, String> ES_CLUSTERS;
    private static boolean ES_ENABLE;
    private static String DB_HOST;
    private static Integer DB_PORT;
    private static String DB_DRIVER;
    private static String DATA_HOME;// 存储文件路径
    private static int SERVER_HEARTBEAT_RATE;// 心跳频率
    private static int SERVER_HEARTBEAT_THRESHOLD;// 心跳失败上限次数
    private static String HOME_URL;
    private static String USER_EXPIRETIME; // 会话超时时间
    private static String RUN_MODE;//运行模式，为空代表PRD模式，可以设为DEVELOP模式，DEVELOP模式影响性能，但有更多提示


    private static String MINIO_URL;
    private static String MINIO_BUCKET;
    private static String MINIO_ACCESSKEY;
    private static String MINIO_SECRETKEY;

    private static String MOBILE_TEST_USER;//移动端测试用户
    private static Boolean IS_MAINTENANCE_MODE;

    static {
        CODEDRIVER_HOME = System.getenv("CODEDRIVER_HOME");
        if (StringUtils.isBlank(CODEDRIVER_HOME)) {
            CODEDRIVER_HOME = System.getProperty("codedriverhome");
            if (StringUtils.isBlank(CODEDRIVER_HOME)) {
                CODEDRIVER_HOME = "/app";
            }
        }
        RUN_MODE = StringUtils.isBlank(System.getProperty("runmode")) ? "PRD" : System.getProperty("runmode");
        try {
            SCHEDULE_SERVER_ID = Integer.parseInt(getProperty(CONFIG_FILE, "schedule.server.id", "1", true));
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

    public static Map<String, String> ES_CLUSTERS() {
        return ES_CLUSTERS;
    }

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

    public static boolean IS_MAINTENANCE_MODE() {
        return IS_MAINTENANCE_MODE;
    }

    public static String RUN_MODE() {
        return RUN_MODE;
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
            MINIO_URL = prop.getProperty("minio.url", "http://192.168.0.10:9001");
            MINIO_ACCESSKEY = prop.getProperty("minio.accesskey", "minioadmin");
            MINIO_SECRETKEY = prop.getProperty("minio.secretkey", "minioadmin");
            MINIO_BUCKET = prop.getProperty("minio.bucket", "codedriver");
            MOBILE_TEST_USER = prop.getProperty("mobile.test.user");
            IS_MAINTENANCE_MODE = Boolean.parseBoolean(prop.getProperty("is.maintenance.mode", "false"));
            ES_ENABLE = Boolean.parseBoolean(prop.getProperty("es.enable", "false"));
            ES_CLUSTERS = new HashMap<>();

            for (Map.Entry<Object, Object> el : prop.entrySet()) {
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
            }

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
