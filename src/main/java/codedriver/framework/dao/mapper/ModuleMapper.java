package codedriver.framework.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import codedriver.framework.dto.ModuleVo;

public interface ModuleMapper {

	public List<ModuleVo> getAllActiveModule();

	public List<ModuleVo> getAllModuleList();

	public List<ModuleVo> getActiveModuleForReferTeam();

	public List<ModuleVo> getActiveModuleListByRest();

	public ModuleVo getModuleById(int id);

	public ModuleVo getModuleByName(String name);

	public void insertModule(ModuleVo module);

	public void updateModule(ModuleVo module);

	public String selectModuleNameById(int moduleId);

	public Integer getModuleStatusByName(String name);

	public List<ModuleVo> getActiveModuleListByMenu(@Param("moduleIdList") List<Integer> moduleIdlist);

}
