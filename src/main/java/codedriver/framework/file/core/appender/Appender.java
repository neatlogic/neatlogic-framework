/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.file.core.appender;

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
