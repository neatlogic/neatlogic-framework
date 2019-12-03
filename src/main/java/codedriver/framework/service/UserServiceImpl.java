package codedriver.framework.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import codedriver.framework.common.config.Config;
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dto.UserExpirationVo;
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
	public int saveUserExpiration(String userId) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date expireDate = new Date(System.currentTimeMillis() + Config.USER_EXPIRETIME * 60 * 1000);
		return userMapper.replaceUserExpiration(new UserExpirationVo(userId,formatter.format(expireDate)));
	}

	@Override
	public int deleteUserExpirationByUserId(String userId) {
		return userMapper.deleteUserExpirationByUserId(userId);
	}

}
