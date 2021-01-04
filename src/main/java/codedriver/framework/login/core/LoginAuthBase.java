package codedriver.framework.login.core;

import codedriver.framework.common.config.Config;
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dto.UserVo;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

public abstract class LoginAuthBase implements ILoginAuth {
    Logger logger = LoggerFactory.getLogger(LoginAuthBase.class);

    public abstract String getType();

    protected static UserMapper userMapper;

    @Autowired
    public void setUserMapper(UserMapper _userMapper) {
        userMapper = _userMapper;
    }

    @Override
    public UserVo auth(HttpServletRequest request, JSONObject jsonObj) {
        String type = this.getType();
        UserVo userVo = null;
        if (StringUtils.isNotBlank(Config.MOBILE_TEST_USER())) {
            String userUuid = Config.MOBILE_TEST_USER();
            userVo = userMapper.getUserByUuid(userUuid);
            type = "test";
        } else {
            userVo = myAuth(request, jsonObj);
        }
        logger.info("loginAuth type: " + type);
        if (userVo != null) {
            logger.info("get userUuId: " + userVo.getUuid());
            logger.info("get userId: " + userVo.getUserId());
        }
        return userVo;
    }

    public abstract UserVo myAuth(HttpServletRequest request, JSONObject jsonObj);

}
