/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.service;

import neatlogic.framework.dto.AuthenticationInfoVo;

import java.util.List;

/**
 * @author linbq
 * @since 2021/8/2 20:43
 **/
public interface AuthenticationInfoService {

    /**
     * 查询用户鉴权时，需要用到到userUuid、teamUuidList、roleUuidList，其中roleUuidList包含用户所在分组的拥护角色列表。
     * @param userUuid
     * @return
     */
    AuthenticationInfoVo getAuthenticationInfo(String userUuid);

    /**
     * 查询用户鉴权时，需要用到到userUuidList、teamUuidList、roleUuidList，其中roleUuidList包含用户所在分组的拥护角色列表。
     * @param userUuidList
     * @return
     */
    AuthenticationInfoVo getAuthenticationInfo(List<String> userUuidList);
}
