package codedriver.framework.dao.mapper;

import codedriver.framework.dto.UserVo;

public interface UserMapper {

	public UserVo getUserByUserIdAndPassword(UserVo userVo);

}
