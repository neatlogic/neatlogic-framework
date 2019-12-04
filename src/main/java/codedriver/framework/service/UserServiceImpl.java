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
	public int saveUserVisit(String userId) {
		if(null != userMapper.getUserVisitByUserId(userId)) {
			return userMapper.updateUserVisit(userId);
		}else {
			return userMapper.insertUserVisit(userId);
		}
	}

	@Override
	public int deleteUserVisitByUserId(String userId) {
		return userMapper.deleteUserVisitByUserId(userId);
	}

}
