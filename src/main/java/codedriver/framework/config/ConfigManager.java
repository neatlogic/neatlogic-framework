package codedriver.framework.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import codedriver.framework.dao.mapper.ConfigMapper;
import codedriver.framework.dto.ConfigVo;

@Component
public class ConfigManager {
	private static ConfigMapper configMapper;

	@Autowired
	public void setConfigMapper(ConfigMapper _configMapper) {
		configMapper = _configMapper;
	}

	public static String getConfig(String key, String defaultValue) {
		ConfigVo configVo = configMapper.getConfigByKey(key);
		if (configVo != null && StringUtils.isNotBlank(configVo.getValue())) {
			return configVo.getValue();
		}
		return defaultValue;
	}
}
