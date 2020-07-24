package codedriver.framework.common.config;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

public class LocalConfig implements BeanFactoryPostProcessor, EnvironmentAware, PriorityOrdered {
	static Logger logger = LoggerFactory.getLogger(LocalConfig.class);
	private static final String CONFIG_FILE = "config.properties";
	private Properties properties;
	private ConfigurableEnvironment environment;

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = (ConfigurableEnvironment) environment;
	}

	private synchronized String getProperty(String keyName, String defaultValue) {
		if (properties == null) {
			properties = new Properties();
			InputStreamReader is = null;
			try {
				is = new InputStreamReader(Config.class.getClassLoader().getResourceAsStream(CONFIG_FILE), "UTF-8");
				properties.load(is);
				if (is != null) {
					is.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				logger.error("load " + CONFIG_FILE + " error: " + ex.getMessage(), ex);
			}
		}
		String value = "";
		if (properties != null) {
			value = properties.getProperty(keyName, defaultValue);
			if (StringUtils.isNotBlank(value)) {
				value = value.trim();
			}
		}

		return value;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		MutablePropertySources propertySources = environment.getPropertySources();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("db.driverClassName", this.getProperty("db.driverClassName", "com.mysql.cj.jdbc.Driver"));
		paramMap.put("db.url", this.getProperty("db.url", "jdbc:mysql://localhost:3306/codedriver?characterEncoding=UTF-8&jdbcCompliantTruncation=false"));
		paramMap.put("db.username", this.getProperty("db.username", "root"));
		paramMap.put("db.password", this.getProperty("db.password", "root"));
		paramMap.put("conn.validationQuery", this.getProperty("conn.validationQuery", "select 1"));
		paramMap.put("conn.testOnBorrow", this.getProperty("conn.testOnBorrow", "true"));
		paramMap.put("conn.maxIdle", this.getProperty("conn.maxIdle", "16"));
		paramMap.put("conn.initialSize", this.getProperty("conn.initialSize", "4"));
		propertySources.addLast(new MapPropertySource("localconfig", paramMap));
	}

	@Override
	public int getOrder() {
		return PriorityOrdered.HIGHEST_PRECEDENCE;
	}

}
