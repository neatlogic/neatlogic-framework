package codedriver.framework.dao.mapper;

import codedriver.framework.dto.ConfigVo;

public interface ConfigMapper {

	public ConfigVo getConfigByKey(String key);
	
	public int relaceConfig(ConfigVo configVo);
	
	public int deleteConfigByKey(String key);

}
