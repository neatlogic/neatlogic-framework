/*
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
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
