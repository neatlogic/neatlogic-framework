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

package neatlogic.framework.lcs.linehandler.core;

import neatlogic.framework.lcs.BaseLineVo;

public interface ILineHandler {

    /**
     * 获取组件英文名
     *
     * @return 组件英文名
     */
    String getHandler();

    /**
     * 获取组件中文名
     *
     * @return 组件中文名
     */
    String getHandlerName();

    /**
     * 获取组件mainBody content|config
     *
     * @param line 行对象
     * @return mainBody content|config
     */
    String getMainBody(BaseLineVo line);

    /**
     * 设置组件mainBody content|config
     *
     * @param line     行对象
     * @param mainBody content|config
     */
    void setMainBody(BaseLineVo line, String mainBody);

    /**
     * 内容是否需要对比
     * @return
     */
    boolean needCompare();

}
