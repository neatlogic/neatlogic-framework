package codedriver.framework.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dto.UserVo;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;

	@Override
	public UserVo getUserByUserIdAndPassword(UserVo userVo) {
		return userMapper.getUserByUserIdAndPassword(userVo);
	}

}
