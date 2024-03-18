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

package neatlogic.framework.file.core.appender;

import ch.qos.logback.core.spi.LifeCycle;

/**
 * 文件追加写入接口
 * @param <E>
 */
public interface Appender<E> extends LifeCycle {

    String getName();

    void setName(String name);

    /**
     * 这是appender完成其工作的地方。请注意，参数的类型为Object。
     * @param event
     */
    void doAppend(E event) throws RuntimeException;
}
