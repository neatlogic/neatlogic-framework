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
     *
     * @return
     */
    Boolean isLimit();

    List<GroupSearchOptionVo> search(GroupSearchVo groupSearchVo);

    List<GroupSearchOptionVo> reload(GroupSearchVo groupSearchVo);
}
