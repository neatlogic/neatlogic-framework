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

package neatlogic.framework.sla.core;

import org.springframework.util.ClassUtils;

/**
 * SLA重算接口，当服务窗口排班更新时，调用该接口的实现类进行相关SLA耗时重算
 */
public interface ISlaRecalculateHandler {

    default String getHandler() {
        return ClassUtils.getUserClass(this.getClass()).getName();
    }

    /**
     * 根据服务窗口uuid重新计算相关的时效
     * @param worktimeUuid 重新排班的服务窗口uuid
     */
    void execute(String worktimeUuid);
}
