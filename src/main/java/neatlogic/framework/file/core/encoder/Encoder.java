/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
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
