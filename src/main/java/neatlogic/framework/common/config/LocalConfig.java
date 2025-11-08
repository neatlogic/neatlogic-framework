/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

    /**
     * 、
     * 更新本地config.properties属性值
     *
     * @param filePath 配置路径
     * @param key      属性key
     * @param newValue 属性新值
     * @throws IOException 异常
     */
    public static void updatePropertyLocal(String filePath, String key, String newValue) throws IOException {
        File file = new File(filePath);
        StringBuilder updatedContent = new StringBuilder();

        // 逐行读取文件内容
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8))) {
            String line;
            boolean keyUpdated = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2 && Objects.equals(parts[0].trim(), key)) {
                    // 替换目标属性值
                    updatedContent.append(key).append("=").append(newValue).append("\n");
                    keyUpdated = true;
                } else {
                    // 保留其他行
                    updatedContent.append(line).append("\n");
                }

            }

            if (!keyUpdated) {
                System.out.println("Key not found: " + key);
            } else {
                // 写回文件
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8))) {
                    writer.write(updatedContent.toString());
                }
            }
        }

    }

    static {
        Properties prop = new Properties();
        String configInfo = null;
        try {
            Properties properties = new Properties();
            String serverAddr = System.getProperty("nacos.home");
            String namespace = System.getProperty("nacos.namespace");
            ConfigService configService = null;
            if (StringUtils.isNotBlank(serverAddr) && StringUtils.isNotBlank(namespace)) {
                properties.put("serverAddr", System.getProperty("nacos.home"));
                properties.put("namespace", System.getProperty("nacos.namespace"));
                configService = NacosFactory.createConfigService(properties);
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
            String dbPassword = prop.getProperty("db.password", "password");
            //加密dbPassword
            if (Boolean.FALSE.equals(RC4Util.isEncrypt(dbPassword))) {
                String dbPasswordChipper = RC4Util.encrypt(dbPassword);
                boolean isPublishOk;
                if (Objects.equals(propertiesFrom, "Nacos")) {
                    assert configInfo != null;
                    String updatedConfig = configInfo.replaceAll("(?m)^db\\.password\\s*=\\s*.*$", "db.password=" + dbPasswordChipper);
                    isPublishOk = configService.publishConfig("config", "neatlogic.framework", updatedConfig);
                } else {
                    // 保存到文件
                    String filePath = Objects.requireNonNull(LocalConfig.class.getClassLoader()
                            .getResource(CONFIG_FILE)).getPath();
                    updatePropertyLocal(filePath, "db.password", dbPasswordChipper);
                    isPublishOk = true;
                }
                if (isPublishOk) {
                    System.out.println("  ✓db.password加密配置更新成功！");
                } else {
                    System.out.println("  ✖db.password加密配置更新失败！");
                }
            } else {
                dbPassword = RC4Util.decrypt(dbPassword);
            }
            dbConfigMap.put("db.password", dbPassword);
            dbConfigMap.put("db.transaction.timeout", prop.getProperty("db.transaction.timeout", "-1"));

            Integer datasourceConnectTimeout = Integer.parseInt(prop.getProperty("datasource.connect.timeout", "5000"));
            Integer datasourceMaximumPoolSize = Integer.parseInt(prop.getProperty("datasource.maximum.pool.size", "20"));
            Long datasourceKeepaliveTime = Long.parseLong(prop.getProperty("datasource.keepalive.time", "180000"));
            Integer datasourceMaxLifetime = Integer.parseInt(prop.getProperty("datasource.max.lifetime", "1800000"));
            Integer datasourceMinimumIdle = Integer.parseInt(prop.getProperty("datasource.minimum.idle", "20"));
            Integer datasourceValidationTimeout = Integer.parseInt(prop.getProperty("datasource.validation.timeout", "5000"));
            Integer datasourceIdleTimeout = Integer.parseInt(prop.getProperty("datasource.idle.timeout", "600000"));
            Integer datasourceLeakDetectionThreshold = Integer.parseInt(prop.getProperty("datasource.leakDetectionThreshold", "10000"));
            dbConfigMap.put("datasource.connect.timeout", datasourceConnectTimeout);
            dbConfigMap.put("datasource.maximum.pool.size", datasourceMaximumPoolSize);
            dbConfigMap.put("datasource.keepalive.time", datasourceKeepaliveTime);
            dbConfigMap.put("datasource.max.lifetime", datasourceMaxLifetime);
            dbConfigMap.put("datasource.minimum.idle", datasourceMinimumIdle);
            dbConfigMap.put("datasource.validation.timeout", datasourceValidationTimeout);
            dbConfigMap.put("datasource.idle.timeout", datasourceIdleTimeout);
            dbConfigMap.put("datasource.leakDetectionThreshold", datasourceLeakDetectionThreshold);
//            dbConfigMap.put("conn.validationQuery", prop.getProperty("conn.validationQuery", "select 1"));
//            dbConfigMap.put("conn.testOnBorrow", prop.getProperty("conn.testOnBorrow", "true"));
//            dbConfigMap.put("conn.maxIdle", prop.getProperty("conn.maxIdle", "16"));
//            dbConfigMap.put("conn.initialSize", prop.getProperty("conn.initialSize", "4"));
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
