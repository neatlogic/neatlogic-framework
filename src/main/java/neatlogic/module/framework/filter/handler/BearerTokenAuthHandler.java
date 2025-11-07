/*
 * Copyright (C) 2025  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.module.framework.filter.handler;

import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.filter.core.LoginAuthHandlerBase;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Service
public class BearerTokenAuthHandler extends LoginAuthHandlerBase {

    @Resource
    private UserMapper userMapper;

    @Override
    public String getType() {
        return "bearertoken";
    }

    @Override
    public UserVo myAuth(HttpServletRequest request) throws Exception {
        String auth = request.getHeader("Authorization");
        if (StringUtils.isNotBlank(auth) && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            return userMapper.getUserByUser(token);
        }
        return null;
    }
}
