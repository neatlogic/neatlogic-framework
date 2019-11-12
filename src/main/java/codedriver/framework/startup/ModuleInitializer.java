package codedriver.framework.startup;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class ModuleInitializer implements WebApplicationInitializer {
	static Logger logger = LoggerFactory.getLogger(ModuleInitializer.class);
	private static String username;
	private static String password;
	private static String url;
	private static Connection dbConnection;
	private static Driver driver;
	private static String driverName;
	private final static String getModuleSql = "SELECT `name`, `description`, `url_mapping`, `config_path`, `is_active`, `version`, `startup` FROM `module`";
	private final static String updateModuleSql = "UPDATE `module` SET `status` = ?,`error` = ? WHERE `name` = ?";

	static {
		Properties pro = new Properties();
		InputStream is = ModuleInitializer.class.getClassLoader().getResourceAsStream("config.properties");
		try {
			pro.load(is);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ex) {
					logger.error(ex.getMessage(), ex);
				}
			}
		}
		driverName = pro.getProperty("db.driverClassName", "com.mysql.jdbc.Driver");
		url = pro.getProperty("db.url", "jdbc:mysql://localhost:3306/codedriver?characterEncoding=UTF-8");
		username = pro.getProperty("db.username", "root");
		password = pro.getProperty("db.password", "root");
	}

	private static void close() {
		if (dbConnection == null) {
			return;
		}
		try {
			dbConnection.close();
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			dbConnection = null;
		}
	}

	private static void open() throws SQLException {
		if (dbConnection != null) {
			return;
		}

		if (driver == null) {
			try {
				Class<?> clazz = Class.forName(driverName);
				driver = ((Driver) clazz.newInstance());
			} catch (Throwable e) {
				throw new SQLException(e.getMessage(), e);
			}
		}

		Properties props = new Properties();
		if (username != null) {
			props.put("user", username);
		}
		if (password != null) {
			props.put("password", password);
		}
		dbConnection = driver.connect(url, props);
		dbConnection.setAutoCommit(true);
		if (dbConnection == null) {
			throw new SQLException();
		}
	}

	@Override
	public void onStartup(ServletContext context) throws ServletException {
		List<Map<String, Object>> moduleList = new ArrayList<Map<String, Object>>();

		try {
			open();
			PreparedStatement st = dbConnection.prepareStatement(getModuleSql);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				Map<String, Object> module = new HashMap<String, Object>();
				module.put("name", rs.getString("name"));
				module.put("description", rs.getString("description"));
				module.put("url_mapping", rs.getString("url_mapping"));
				module.put("is_active", rs.getInt("is_active"));
				module.put("config_path", rs.getString("config_path"));
				module.put("startup", rs.getInt("startup"));
				moduleList.add(module);
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		} finally {
			close();
		}
		for (Map<String, Object> module : moduleList) {
			InputStream is = null;
			try {
				if (Integer.parseInt(module.get("is_active").toString()) == 1) {
					XmlWebApplicationContext appContext = new XmlWebApplicationContext();
					appContext.setConfigLocation("classpath*:" + module.get("config_path").toString());
					appContext.setId(module.get("name").toString());
					is = this.getClass().getClassLoader().getResourceAsStream(module.get("config_path").toString());
					if (is == null) {
						module.put("error", "找不到模块组件。");
						module.put("status", "failed");
						module.put("version", "");
						continue;
					}

					ServletRegistration.Dynamic sr = context.addServlet(module.get("name").toString(), new DispatcherServlet(appContext));
					sr.setLoadOnStartup(Integer.parseInt(module.get("startup").toString()));
					String urlmapping = module.get("url_mapping").toString();
					if (urlmapping.indexOf(",") > -1) {
						String[] urls = urlmapping.split(",");
						for (String url : urls) {
							sr.addMapping(url);
						}
					} else {
						sr.addMapping(urlmapping);
					}
					module.put("status", "success");
					module.put("error", "");
				} else {
					module.put("error", "");
					module.put("version", "");
				}
			} catch (Exception ex) {
				module.put("error", ex.getMessage());
				module.put("status", "failed");
				logger.error(ex.getMessage(), ex);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
		}
		try {
			open();
			PreparedStatement st = dbConnection.prepareStatement(updateModuleSql);
			for (Map<String, Object> module : moduleList) {
				st.setString(1, module.get("status").toString());
				st.setString(2, module.get("error").toString());
				st.setString(3, module.get("name").toString());
				st.execute();
			}
			st.close();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		} finally {
			close();
		}
	}
}
