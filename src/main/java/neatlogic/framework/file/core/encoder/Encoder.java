/*
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
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
