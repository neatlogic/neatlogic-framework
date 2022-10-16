/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package codedriver.framework.file.core.pattern;

/**
 * 为派生类设置通用接口的最小转换器。它还实现在链接列表中链接转换器的功能。
 *
 */
abstract public class Converter<E> {

    Converter<E> next;

    /**
     * convert方法负责从事件中提取数据，并将其存储以供写入方法稍后使用。
     *
     * @param event
     */
    public abstract String convert(E event);

    /**
     * 在最简单的形式中，convert只是将从事件中提取的数据附加到作为参数传递的缓冲区中。
     *
     * @param buf 附加数据的输入缓冲区
     * @param event 从中提取数据的事件
     */
    public void write(StringBuilder buf, E event) {
        buf.append(convert(event));
    }

    public final void setNext(Converter<E> next) {
        if (this.next != null) {
            throw new IllegalStateException("Next converter has been already set");
        }
        this.next = next;
    }

    public final Converter<E> getNext() {
        return next;
    }
}
