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

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.dao.cache.UserSessionCache;
import neatlogic.framework.dao.mapper.UserSessionMapper;
import neatlogic.framework.dto.UserSessionVo;
import neatlogic.framework.heartbeat.dao.mapper.ServerMapper;
import neatlogic.framework.heartbeat.dto.ServerClusterVo;
import neatlogic.framework.integration.authentication.enums.AuthenticateType;
import neatlogic.framework.util.HttpRequestUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserSessionServiceImpl implements UserSessionService {

    @Resource
    private UserSessionMapper userSessionMapper;

    @Resource
    private ServerMapper serverMapper;

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

    @Override
    public void deleteOtherClusterUserSessionByTokenList(List<String> removeTokenList) {
        List<ServerClusterVo> serverVos = serverMapper.getAllServerList();
        for (ServerClusterVo serverVo : serverVos) {
            if (!Objects.equals(serverVo.getServerId(), Config.SCHEDULE_SERVER_ID) && Objects.equals(serverVo.getStatus(), ServerClusterVo.STARTUP)) {
                JSONObject param = new JSONObject();
                param.put("tokenHashList", removeTokenList);
                HttpRequestUtil.post(serverVo.getHost() + "/neatlogic/api/rest/user/session/cache/clear").setAuthType(AuthenticateType.BUILDIN).setPayload(param.toJSONString()).setReadTimeout(5000).setConnectTimeout(5000).sendRequest();
            }
        }
    }

    @Override
    public void getOtherClusterUserSessionByTokenList(List<String> removeTokenList) {
        List<ServerClusterVo> serverVos = serverMapper.getAllServerList();
        for (ServerClusterVo serverVo : serverVos) {
            if (!Objects.equals(serverVo.getServerId(), Config.SCHEDULE_SERVER_ID) && Objects.equals(serverVo.getStatus(), ServerClusterVo.STARTUP)) {
                JSONObject param = new JSONObject();
                param.put("tokenHashList", removeTokenList);
                HttpRequestUtil.post(serverVo.getHost() + "/neatlogic/api/rest/user/session/cache/clear").setAuthType(AuthenticateType.BUILDIN).setPayload(param.toJSONString()).setReadTimeout(5000).setConnectTimeout(5000).sendRequest();
            }
        }
    }

}
