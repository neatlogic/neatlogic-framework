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
package codedriver.framework.file.core.util;

import codedriver.framework.file.core.pattern.CompositeConverter;
import codedriver.framework.file.core.pattern.Converter;
import codedriver.framework.file.core.pattern.DynamicConverter;

public class ConverterUtil {

    /**
     * 启动转换器链中的转换器。
     *
     * @param head
     */
    public static <E> void startConverters(Converter<E> head) {
        Converter<E> c = head;
        while (c != null) {
            // CompositeConverter is a subclass of DynamicConverter
            if (c instanceof CompositeConverter) {
                CompositeConverter<E> cc = (CompositeConverter<E>) c;
                Converter<E> childConverter = cc.getChildConverter();
                startConverters(childConverter);
                cc.start();
            } else if (c instanceof DynamicConverter) {
                DynamicConverter<E> dc = (DynamicConverter<E>) c;
                dc.start();
            }
            c = c.getNext();
        }
    }

    public static <E> Converter<E> findTail(Converter<E> head) {
        Converter<E> p = head;
        while (p != null) {
            Converter<E> next = p.getNext();
            if (next == null) {
                break;
            } else {
                p = next;
            }
        }
        return p;
    }
}
