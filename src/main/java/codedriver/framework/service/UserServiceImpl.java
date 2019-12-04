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

	@Override
	public int saveUserSession(String userId) {
		if(null != userMapper.getUserSessionByUserId(userId)) {
			return userMapper.updateUserSession(userId);
		}else {
			return userMapper.insertUserSession(userId);
		}
	}

	@Override
	public int deleteUserSessionByUserId(String userId) {
		return userMapper.deleteUserSessionByUserId(userId);
	}

}
