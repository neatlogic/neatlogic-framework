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

    private static  String propertiesFrom;

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
            dbConfigMap.put("jms.user", prop.getProperty("jms.user","neatlogic"));
            dbConfigMap.put("jms.password", prop.getProperty("jms.password","123456"));
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
