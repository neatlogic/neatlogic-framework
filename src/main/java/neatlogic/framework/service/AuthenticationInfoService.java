/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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
     * 补充父级组
     * @param teamUuidList 组
     */
    Set<String> getTeamSetWithParents(List<String> teamUuidList);
}
