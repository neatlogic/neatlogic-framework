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
 * 当要求转换整数时，IntegerTokenConverter将转换该整数的字符串值。
 * 用于生成归档文件名称，filename.log.%i 转换成 filename.log.3
 */
public class IntegerTokenConverter extends DynamicConverter<Object> implements MonoTypedConverter {

    public final static String CONVERTER_KEY = "i";

    public String convert(int i) {
        String s = Integer.toString(i);
        FormatInfo formattingInfo = getFormattingInfo();
        if (formattingInfo == null) {
            return s;
        }
        // 下面是在数字前面补指定数量个0
        int min = formattingInfo.getMin();
        StringBuilder sbuf = new StringBuilder();
        for (int j = s.length(); j < min; ++j) {
            sbuf.append('0');
        }
        return sbuf.append(s).toString();
    }

    public String convert(Object o) {
        if (o == null) {
            throw new IllegalArgumentException("Null argument forbidden");
        }
        if (o instanceof Integer) {
            Integer i = (Integer) o;
            return convert(i.intValue());
        }
        throw new IllegalArgumentException("Cannot convert " + o + " of type" + o.getClass().getName());
    }

    public boolean isApplicable(Object o) {
        return (o instanceof Integer);
    }
}
