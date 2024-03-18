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

package neatlogic.framework.file.core.layout;

import ch.qos.logback.core.spi.LifeCycle;

public interface Layout<E> extends LifeCycle {
    /**
     * 转换一个事件（Object类型），并在适当格式化后将其作为String返回。
     *
     * <p>接收对象并返回String是格式化事件的最不复杂的方法。然而，它具有显著的CPU效率。
     * </p>
     *
     * @param event 要格式化的事件
     * @return
     */
    String doLayout(E event);

    /**
     * 返回此布局的文件头。返回的值可能为空。
     * @return The header.
     */
    String getFileHeader();

    /**
     * 返回此布局的文件页脚。返回的值可能为空。
     * @return The footer.
     */
    String getFileFooter();

    /**
     * 返回适用于实现的内容类型。
     *
     * @return
     */
    String getContentType();
}
