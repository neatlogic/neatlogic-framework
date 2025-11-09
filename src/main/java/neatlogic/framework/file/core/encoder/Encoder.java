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
