package codedriver.framework.common.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;

import codedriver.framework.common.RootComponent;

@RootComponent
@Order(Ordered.HIGHEST_PRECEDENCE) // 配置文件类第一个加载
public class Config {
	private static Logger logger = LoggerFactory.getLogger(Config.class);
	@NacosInjected
	private ConfigService configService;
	private static final String CONFIG_FILE = "config.properties";

	public static final int SCHEDULE_SERVER_ID;
	public static final String RESPONSE_TYPE_JSON = "application/json;charset=UTF-8";
	public static final String RESPONSE_TYPE_HTML = "text/html;charset=UTF-8";
	public static final String RESPONSE_TYPE_TEXT = "text/plain;charset=UTF-8";
	private static String JWT_SECRET = "techsure#codedriver$secret";
	private static String CODEDRIVER_HOME;
	private static Map<String, String> ES_CLUSTERS;
	private static boolean ES_ENABLE;
	private static String DATA_HOME;// 存储文件路径
	private static int SERVER_HEARTBEAT_RATE = 1;// 心跳频率
	private static int SERVER_HEARTBEAT_THRESHOLD = 3;// 心跳失败上限次数
	private static String HOME_URL;

	static {
		CODEDRIVER_HOME = System.getenv("CODEDRIVER_HOME");
		if (StringUtils.isBlank(CODEDRIVER_HOME)) {
			CODEDRIVER_HOME = "/app";
		}
		try {
			SCHEDULE_SERVER_ID = Integer.parseInt(getProperty(CONFIG_FILE, "schedule.server.id", "1"));
		} catch (Exception ex) {
			logger.error("【配置文件初始化失败】请在" + CONFIG_FILE + "中配置schedule.server.id变量");
			System.out.println("【配置文件初始化失败】请在" + CONFIG_FILE + "中配置schedule.server.id变量");
			throw ex;
		}
	}
	
	public Config() {
		System.out.println("-----------------------init config");
	}

	public static final String CODEDRIVER_HOME() {
		return CODEDRIVER_HOME;
	}

	public static final String JWT_SECRET() {
		return JWT_SECRET;
	}

	public static final Map<String, String> ES_CLUSTERS() {
		return ES_CLUSTERS;
	}

	public static final boolean ES_ENABLE() {
		return ES_ENABLE;
	}

	public static final String DATA_HOME() {
		return DATA_HOME;
	}

	public static final int SERVER_HEARTBEAT_RATE() {
		return SERVER_HEARTBEAT_RATE;
	}

	public static final int SERVER_HEARTBEAT_THRESHOLD() {
		return SERVER_HEARTBEAT_THRESHOLD;
	}

	public static final String HOME_URL() {
		return HOME_URL;
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
			prop.load(new ByteArrayInputStream(configInfo.getBytes()));
			DATA_HOME = prop.getProperty("data.home", "/app/data");
			SERVER_HEARTBEAT_RATE = Integer.parseInt(prop.getProperty("heartbeat.rate", "1"));
			SERVER_HEARTBEAT_THRESHOLD = Integer.parseInt(prop.getProperty("heartbeat.threshold", "3"));
			System.out.println("--------------------------------------------- config-init--------------------------------------------------------");
			HOME_URL = prop.getProperty("home.url");
			JWT_SECRET = prop.getProperty("jwt.secret", "techsure#codedriver$secret");

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

	private static String getProperty(String configFile, String keyName, String defaultValue) {
		Properties pro = new Properties();
		try (InputStream is = Config.class.getClassLoader().getResourceAsStream(configFile)) {
			pro.load(is);
			String value = pro.getProperty(keyName, defaultValue);
			if (value != null) {
				value = value.trim();
			}
			return value;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static String getProperty(String configFile, String keyName) {
		return getProperty(configFile, keyName, "");
	}

}
