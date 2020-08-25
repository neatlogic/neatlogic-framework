package codedriver.framework.common.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config_bak {
	private static Logger logger = LoggerFactory.getLogger(Config.class);
	public static String JWT_SECRET = "techsure#codedriver$secret";
	public static final String RESPONSE_TYPE_JSON = "application/json;charset=UTF-8";
	public static final String RESPONSE_TYPE_HTML = "text/html;charset=UTF-8";
	public static final String RESPONSE_TYPE_TEXT = "text/plain;charset=UTF-8";
	public static final int SCHEDULE_SERVER_ID;
	public static String CODEDRIVER_HOME;
	public static final int SERVER_HEARTBEAT_RATE;// 默认1分钟
	public static final int SERVER_HEARTBEAT_THRESHOLD;// 默认3
	public static String DATA_HOME;// 数据文件根路径
	public static final Map<String, String> ES_CLUSTERS;
	public static final boolean ES_ENABLE;
	public static final String HOME_URL;

	private static final String CONFIG_FILE = "config.properties";

	static {
		CODEDRIVER_HOME = System.getenv("CODEDRIVER_HOME");
		if (StringUtils.isBlank(CODEDRIVER_HOME)) {
			CODEDRIVER_HOME = "/app";
		}
		DATA_HOME = getProperty(CONFIG_FILE, "data.home");
		if (StringUtils.isBlank(DATA_HOME)) {
			DATA_HOME = "/app/data";
		}
		try {
			SCHEDULE_SERVER_ID = Integer.parseInt(getProperty(CONFIG_FILE, "schedule.server.id", "1"));
		} catch (Exception ex) {
			System.out.println("【配置文件初始化失败】请在" + CONFIG_FILE + "中配置schedule.server.id变量");
			throw ex;
		}
		try {
			SERVER_HEARTBEAT_RATE = Integer.parseInt(getProperty(CONFIG_FILE, "server.heartbeat.rate", "1"));
		} catch (Exception ex) {
			System.out.println("【配置文件初始化失败】请在" + CONFIG_FILE + "中配置server.heart.rate变量");
			throw ex;
		}
		try {
			SERVER_HEARTBEAT_THRESHOLD = Integer.parseInt(getProperty(CONFIG_FILE, "server.heartbeat.threshold", "3"));
		} catch (Exception ex) {
			System.out.println("【配置文件初始化失败】请在" + CONFIG_FILE + "中配置server.heart.threshold变量");
			throw ex;
		}
		try {
			HOME_URL = getProperty(CONFIG_FILE, "home.url", "");
		} catch (Exception ex) {
			System.out.println("【配置文件初始化失败】请在" + CONFIG_FILE + "中配置home.url变量");
			throw ex;
		}
		boolean esEnable = false;
		Map<String, String> map = null;
		try (InputStream in = Config.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
			if (in == null) {
				map = Collections.emptyMap();
			} else {
				Properties props = new Properties();
				props.load(in);

				map = new HashMap<>(4);
				for (Map.Entry<Object, Object> el : props.entrySet()) {
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
					map.put(clusterName, value);
				}

				esEnable = Boolean.parseBoolean(props.getProperty("es.enable"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			ES_CLUSTERS = map == null || map.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(map);
			ES_ENABLE = esEnable;
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
