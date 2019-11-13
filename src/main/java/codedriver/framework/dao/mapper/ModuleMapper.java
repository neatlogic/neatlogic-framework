package codedriver.framework.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import codedriver.framework.dto.ModuleVo;

public interface ModuleMapper {

	public List<ModuleVo> getAllModuleList();

}
