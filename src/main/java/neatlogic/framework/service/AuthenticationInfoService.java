/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.service;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.dto.AuthenticationInfoVo;

import java.util.List;
import java.util.Set;

/**
 * @author linbq
 * @since 2021/8/2 20:43
 **/
public interface AuthenticationInfoService {

    /**
     * 查询用户鉴权时，需要用到到userUuid、teamUuidList、roleUuidList，其中roleUuidList包含用户所在分组的拥护角色列表。无需过滤不符合规则的角色
     *
     * @param userUuid 用户uuid
     */
    AuthenticationInfoVo getAuthenticationInfo(String userUuid);

    /**
     * 查询用户鉴权时，需要用到到userUuidList、teamUuidList、roleUuidList，其中roleUuidList包含用户所在分组的拥护角色列表。
     *
     * @param userUuidList 用户uuid列表
     */
    AuthenticationInfoVo getAuthenticationInfo(List<String> userUuidList);

    /**
     * 查询用户鉴权时，需要用到到userUuid、teamUuidList、roleUuidList，其中roleUuidList包含用户所在分组的拥护角色列表。可以控制是否过滤不符合规则的角色
     *
     * @param userUuid   用户uuid
     * @param isRuleRole 是否过滤不符合规则的角色
     */
    AuthenticationInfoVo getAuthenticationInfo(String userUuid, Boolean isRuleRole);

    /**
     * 查询用户鉴权时，需要用到到userUuid、teamUuidList、roleUuidList，其中roleUuidList包含用户所在分组的拥护角色列表。可以控制是否过滤不符合规则的角色
     *
     * @param userUuid   用户uuid
     * @param isRuleRole 是否过滤不符合规则的角色
     * @param originHeader 历史header， 不为空则无需获取当前请求的header
     */
    AuthenticationInfoVo getAuthenticationInfo(String userUuid, Boolean isRuleRole, JSONObject originHeader);

    /**
     * 补充父级组
     * @param teamUuidList 组
     */
    Set<String> getTeamSetWithParents(List<String> teamUuidList);
}
