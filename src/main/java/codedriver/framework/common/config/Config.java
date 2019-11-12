package codedriver.framework.common.config;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {
	
	private static Logger logger = LoggerFactory.getLogger(Config.class);
	
	public static String TECHSURE_HOME;
	
	private static final String CONFIG_FILE = "config.properties";
	public static final String SEPARATOR = System.getProperty("file.separator");
	public static String REST_AUDIT_PATH;
	public static final String RESPONSE_TYPE_JSON = "application/json;charset=UTF-8";
	public static final String RESPONSE_TYPE_HTML = "text/html;charset=UTF-8";
	public static final String RESPONSE_TYPE_TEXT = "text/plain;charset=UTF-8";
	/******************** schedule ***********************************/
	public static final int SCHEDULE_SERVER_ID;
	public static final String SCHEDULE_AUDIT_PATH;	
	/*****************************************************************/
	static {
		REST_AUDIT_PATH = "/app/codedriver/";
		TECHSURE_HOME = System.getenv("TECHSURE_HOME");
		if (TECHSURE_HOME == null || "".equals(TECHSURE_HOME)) {
			TECHSURE_HOME = "/app";
		}
		try {
			SCHEDULE_SERVER_ID = Integer.parseInt(getProperty(CONFIG_FILE, "schedule.server.id", "1"));
		} catch (Exception ex) {
			System.out.println("【配置文件初始化失败】请在" + CONFIG_FILE + "中配置schedule.server.id变量");
			throw ex;
		}
		try {
			String scheduleAuditPath = getProperty(CONFIG_FILE, "schedule.audit.path");
			SCHEDULE_AUDIT_PATH = TECHSURE_HOME + File.separator + (scheduleAuditPath.endsWith(SEPARATOR) ? scheduleAuditPath : scheduleAuditPath + SEPARATOR);
		} catch (Exception ex) {
			System.out.println("【配置文件初始化失败】请在" + CONFIG_FILE + "中配置schedule.audit.path变量");
			throw ex;
		}
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
