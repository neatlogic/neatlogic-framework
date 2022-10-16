/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.file.core.encoder;

import ch.qos.logback.core.spi.LifeCycle;

public interface Encoder<E> extends LifeCycle {

    /**
     * 将事件编码为字节。
     *
     * @param event
     */
    byte[] encode(E event);
}
