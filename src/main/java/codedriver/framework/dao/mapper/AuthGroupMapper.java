package codedriver.framework.dao.mapper;

import java.util.List;

public interface AuthGroupMapper {
	
	public List<String> getRoleNameListByAuthGroupName(String authGroupName);
}
