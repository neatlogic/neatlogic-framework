package codedriver.framework.common.config;

import org.springframework.context.annotation.Configuration;

import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;

@Configuration
@EnableNacosConfig(globalProperties = @NacosProperties(serverAddr = "${nacos.home}", namespace = "${nacos.namespace}"))
//@NacosPropertySource(dataId = "config", groupId = "codedriver.framework", autoRefreshed = true)
public class NacosConfig {
}
