package neatlogic.framework.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import neatlogic.framework.dao.mapper.ConfigMapper;
import neatlogic.framework.dto.ConfigVo;

@Component
public class ConfigManager {
	private static ConfigMapper configMapper;

	@Autowired
	public void setConfigMapper(ConfigMapper _configMapper) {
		configMapper = _configMapper;
	}

	public static String getConfig(ITenantConfig tenantConfig) {
		ConfigVo configVo = configMapper.getConfigByKey(tenantConfig.getKey());
		if (configVo != null) {
			return configVo.getValue();
		}
		return tenantConfig.getValue();
	}
}
