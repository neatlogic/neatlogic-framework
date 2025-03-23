/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.restful.groupsearch.core;

import java.util.List;

public interface IGroupSearchHandler {
    String getName();

    String getLabel();

    default String getHeader() {
        return getName() + "#";
    }

    int getSort();

    /**
     * 是否受总数限制
     */
    Boolean isLimit();

    /**
     * 搜索用户时触发
     *
     * @param groupSearchVo 关键字
     * @return 用户列表
     */
    List<GroupSearchOptionVo> search(GroupSearchVo groupSearchVo);

    /**
     * 回显用户选择控件时触发
     *
     * @param groupSearchVo 关键字
     * @return 用户列表
     */
    List<GroupSearchOptionVo> reload(GroupSearchVo groupSearchVo);
}
