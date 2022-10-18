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
 * 实现此功能以对PatternLayout执行编译后处理。
 *
 * 例如，经典模块中的PatternLayout应该添加一个用于异常处理的转换器（否则将不会打印异常）。
 *
 */
public interface PostCompileProcessor<E> {

    /**
     * 转换器链的编译后处理。
     *
     * @param head 链中的第一个转换器
     */
    void process(Converter<E> head);
}
