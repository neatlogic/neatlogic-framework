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

package neatlogic.framework.file.core.encoder;

import ch.qos.logback.core.spi.LifeCycle;

public interface Encoder<E> extends LifeCycle {

    /**
     * 获取标头字节。此方法通常在打开输出流时调用。
     *
     * @return
     */
    byte[] headerBytes();

    /**
     * 将事件编码为字节。
     *
     * @param event
     */
    byte[] encode(E event);

    /**
     * 获取页脚字节。此方法通常在关闭写入事件的流之前调用。
     *
     * @return
     */
    byte[] footerBytes();
}
