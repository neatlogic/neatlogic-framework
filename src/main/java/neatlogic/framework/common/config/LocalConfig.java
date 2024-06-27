/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.common.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import neatlogic.framework.common.util.RC4Util;
import neatlogic.framework.util.I18nUtils;
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

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class LocalConfig implements BeanFactoryPostProcessor, EnvironmentAware, PriorityOrdered {
    static Logger logger = LoggerFactory.getLogger(LocalConfig.class);
    private static final String CONFIG_FILE = "config.properties";

    private static String propertiesFrom;

    public static String getPropertiesFrom() {
        return propertiesFrom;
    }

    private Properties properties;
    private ConfigurableEnvironment environment;

    public static final Map<String, Object> dbConfigMap = new HashMap<>();

    static {
        Properties prop = new Properties();
        String configInfo = null;
        try {
            Properties properties = new Properties();
            String serverAddr = System.getProperty("nacos.home");
            String namespace = System.getProperty("nacos.namespace");
            if (StringUtils.isNotBlank(serverAddr) && StringUtils.isNotBlank(namespace)) {
                properties.put("serverAddr", System.getProperty("nacos.home"));
                properties.put("namespace", System.getProperty("nacos.namespace"));
                ConfigService configService = NacosFactory.createConfigService(properties);
                configInfo = configService.getConfig("config", "neatlogic.framework", 3000);
                if (StringUtils.isNotBlank(configInfo)) {
                    prop.load(new InputStreamReader(new ByteArrayInputStream(configInfo.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
                    System.out.println("⚡" + I18nUtils.getStaticMessage("common.startloadconfig", "Nacos", System.getProperty("nacos.home"), System.getProperty("nacos.namespace")));
                    propertiesFrom = "Nacos";
                }
            }

            if (StringUtils.isBlank(configInfo)) {
                // 如果从nacos中读不出配置，则使用本地配置文件配置
                try {
                    prop.load(new InputStreamReader(Objects.requireNonNull(Config.class.getClassLoader().getResourceAsStream(CONFIG_FILE)), StandardCharsets.UTF_8));
                    System.out.println("⚡" + I18nUtils.getStaticMessage("common.startloadconfig", "config.properties"));
                    propertiesFrom = "config.properties";
                } catch (Exception ex) {
                    System.out.println("ERROR: " + I18nUtils.getStaticMessage("nfe.confignotfoundexception.confignotfoundexception"));
                    System.exit(1);
                }
            }

            dbConfigMap.put("db.driverClassName", prop.getProperty("db.driverClassName", "com.mysql.cj.jdbc.Driver"));
            dbConfigMap.put("db.url", prop.getProperty("db.url", "jdbc:mysql://localhost:3306/neatlogic?characterEncoding=UTF-8&jdbcCompliantTruncation=false"));
            dbConfigMap.put("db.username", prop.getProperty("db.username", "username"));
            dbConfigMap.put("db.password", RC4Util.decrypt(prop.getProperty("db.password", "password")));
            dbConfigMap.put("db.transaction.timeout", prop.getProperty("db.transaction.timeout", "-1"));

            Integer datasourceConnectTimeout = Integer.parseInt(prop.getProperty("datasource.connect.timeout", "5000"));
            Integer datasourceMaximumPoolSize = Integer.parseInt(prop.getProperty("datasource.maximum.pool.size", "20"));
            Long datasourceKeepaliveTime = Long.parseLong(prop.getProperty("datasource.keepalive.time", "600000"));
            Integer datasourceMaxLifetime = Integer.parseInt(prop.getProperty("datasource.max.lifetime", "1800000"));
            Integer datasourceMinimumIdle = Integer.parseInt(prop.getProperty("datasource.minimum.idle", "20"));
            Integer datasourceValidationTimeout = Integer.parseInt(prop.getProperty("datasource.validation.timeout", "5000"));
            Integer datasourceIdleTimeout = Integer.parseInt(prop.getProperty("datasource.idle.timeout", "600000"));
            dbConfigMap.put("datasource.connect.timeout", datasourceConnectTimeout);
            dbConfigMap.put("datasource.maximum.pool.size", datasourceMaximumPoolSize);
            dbConfigMap.put("datasource.keepalive.time", datasourceKeepaliveTime);
            dbConfigMap.put("datasource.max.lifetime", datasourceMaxLifetime);
            dbConfigMap.put("datasource.minimum.idle", datasourceMinimumIdle);
            dbConfigMap.put("datasource.validation.timeout", datasourceValidationTimeout);
            dbConfigMap.put("datasource.idle.timeout", datasourceIdleTimeout);
//            dbConfigMap.put("conn.validationQuery", prop.getProperty("conn.validationQuery", "select 1"));
//            dbConfigMap.put("conn.testOnBorrow", prop.getProperty("conn.testOnBorrow", "true"));
//            dbConfigMap.put("conn.maxIdle", prop.getProperty("conn.maxIdle", "16"));
//            dbConfigMap.put("conn.initialSize", prop.getProperty("conn.initialSize", "4"));
            String mongoHost = prop.getProperty("mongo.host", "localhost:27017");
            String mongoUser = prop.getProperty("mongo.username", "root");
            String mongoPwd = prop.getProperty("mongo.password", "root");
            String mongoDb = prop.getProperty("mongo.database", "admin");
            mongoPwd = RC4Util.decrypt(mongoPwd);
            dbConfigMap.put("mongo.url", "mongodb://" + mongoUser + ":" + mongoPwd + "@" + mongoHost + "/" + mongoDb);
            dbConfigMap.put("jms.url", prop.getProperty("jms.url", "tcp://localhost:61616"));
            dbConfigMap.put("jms.user", prop.getProperty("jms.user", "neatlogic"));
            dbConfigMap.put("jms.password", prop.getProperty("jms.password", "123456"));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        MutablePropertySources propertySources = environment.getPropertySources();
        propertySources.addLast(new MapPropertySource("localconfig", dbConfigMap));
    }

    @Override
    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }

}
