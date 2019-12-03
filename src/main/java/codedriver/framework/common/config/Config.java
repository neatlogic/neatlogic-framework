package codedriver.framework.common.config;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {
	private static Logger logger = LoggerFactory.getLogger(Config.class);
	public static String JWT_SECRET = "techsure#codedriver$secret";
	public static String REST_AUDIT_PATH;
	public static final String RESPONSE_TYPE_JSON = "application/json;charset=UTF-8";
	public static final String RESPONSE_TYPE_HTML = "text/html;charset=UTF-8";
	public static final String RESPONSE_TYPE_TEXT = "text/plain;charset=UTF-8";
	public static final int SCHEDULE_SERVER_ID;
	public static String CODEDRIVER_HOME;
	public static final int SERVER_HEARTBEAT_RATE;// 默认3分钟
	public static final int SERVER_HEARTBEAT_THRESHOLD;// 默认5
	public static final int USER_EXPIRETIME;//登录有效时间段

	private static final String CONFIG_FILE = "config.properties";
	static {
		REST_AUDIT_PATH = "/app/codedriver/";
		CODEDRIVER_HOME = System.getenv("CODEDRIVER_HOME");
		if (CODEDRIVER_HOME == null || "".equals(CODEDRIVER_HOME)) {
			CODEDRIVER_HOME = "/app";
		}
		try {
			SCHEDULE_SERVER_ID = Integer.parseInt(getProperty(CONFIG_FILE, "schedule.server.id", "1"));
		} catch (Exception ex) {
			System.out.println("【配置文件初始化失败】请在" + CONFIG_FILE + "中配置schedule.server.id变量");
			throw ex;
		}
		try {
			SERVER_HEARTBEAT_RATE = Integer.parseInt(getProperty(CONFIG_FILE, "server.heartbeat.rate", "3"));
		} catch (Exception ex) {
			System.out.println("【配置文件初始化失败】请在" + CONFIG_FILE + "中配置server.heart.rate变量");
			throw ex;
		}
		try {
			SERVER_HEARTBEAT_THRESHOLD = Integer.parseInt(getProperty(CONFIG_FILE, "server.heartbeat.threshold", "5"));
		} catch (Exception ex) {
			System.out.println("【配置文件初始化失败】请在" + CONFIG_FILE + "中配置server.heart.threshold变量");
			throw ex;
		}
		USER_EXPIRETIME = Integer.parseInt(getProperty(CONFIG_FILE, "user.expireTime", "30"));
	}

	private static String getProperty(String configFile, String keyName, String defaultValue) {
		Properties pro = new Properties();
		InputStreamReader is = null;
		try {
			is = new InputStreamReader(Config.class.getClassLoader().getResourceAsStream(configFile), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		}
		try {
			pro.load(is);
			if (is != null) {
				is.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("load " + configFile + " error: " + ex.getMessage(), ex);
		}
		String value = pro.getProperty(keyName, defaultValue);
		if (value != null) {
			value = value.trim();
		}

		return value;
	}

	public static String getProperty(String configFile, String keyName) {
		Properties pro = new Properties();
		InputStream is = Config.class.getClassLoader().getResourceAsStream(configFile);
		String value = null;
		if (is != null) {
			try {
				pro.load(is);
				if (is != null) {
					is.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				logger.error("load " + configFile + " error: " + ex.getMessage(), ex);
			}
			value = pro.getProperty(keyName);
		}

		if (value != null) {
			value = value.trim();
		}

		return value;
	}
}
