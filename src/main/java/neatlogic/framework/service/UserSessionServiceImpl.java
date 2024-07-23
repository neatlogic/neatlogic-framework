/*Copyright (C) 2023  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.service;

import neatlogic.framework.dao.cache.UserSessionCache;
import neatlogic.framework.dao.mapper.UserSessionMapper;
import neatlogic.framework.dto.UserSessionVo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserSessionServiceImpl implements UserSessionService {

    @Resource
    private UserSessionMapper userSessionMapper;

    @Override
    public void updateUserSessionAuthInfoHashByTokenHash(String tokenHash, String authInfoHash) {
        userSessionMapper.updateUserSessionAuthInfoHashByTokenHash(tokenHash, authInfoHash);
    }

    @Override
    public void deleteUserSessionByUserUuid(List<String> userUuidList) {
        if (CollectionUtils.isNotEmpty(userUuidList)) {
            List<UserSessionVo> userSessionVoList = userSessionMapper.getUserSessionByUuidList(userUuidList);
            if (CollectionUtils.isNotEmpty(userSessionVoList)) {
                userSessionMapper.deleteUserSessionByTokenHashList(userSessionVoList.stream().map(UserSessionVo::getTokenHash).collect(Collectors.toList()));
                for (UserSessionVo sessionVo : userSessionVoList) {
                    //禁用用户时删除userSession
                    UserSessionCache.removeItem(sessionVo.getTokenHash());
                }
            }
        }
    }

}
