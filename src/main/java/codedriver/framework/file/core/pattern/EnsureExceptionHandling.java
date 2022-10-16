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

import codedriver.framework.file.core.IEvent;
import codedriver.framework.file.core.util.ConverterUtil;

public class EnsureExceptionHandling implements PostCompileProcessor<IEvent> {

    /**
     * This implementation checks if any of the converters in the chain handles
     * exceptions. If not, then this method adds a
     * {link ExtendedThrowableProxyConverter} instance to the end of the chain.
     * <p>
     * This allows appenders using this layout to output exception information
     * event if the user forgets to add %ex to the pattern. Note that the
     * appenders defined in the Core package are not aware of exceptions nor
     * LoggingEvents.
     * <p>
     * If for some reason the user wishes to NOT print exceptions, then she can
     * add %nopex to the pattern.
     *
     *
     */
    public void process(Converter<IEvent> head) {
        if (head == null) {
            // this should never happen
            throw new IllegalArgumentException("cannot process empty chain");
        }
        Converter<IEvent> tail = ConverterUtil.findTail(head);
        Converter<IEvent> exConverter = null;
        tail.setNext(exConverter);
    }

}
