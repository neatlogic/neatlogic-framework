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
package codedriver.framework.file.core.rolling;

import ch.qos.logback.core.pattern.util.AlmostAsIsEscapeUtil;
import ch.qos.logback.core.rolling.helper.FileFilterUtil;
import ch.qos.logback.core.spi.ScanException;
import codedriver.framework.file.core.pattern.*;
import codedriver.framework.file.core.pattern.parser.Node;
import codedriver.framework.file.core.pattern.parser.Parser;
//import codedriver.framework.file.core.util.AlmostAsIsEscapeUtil;
import codedriver.framework.file.core.util.ConverterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 在解析文件名模式后，给定一个数字或日期，可以使用此类的实例根据文件名模式和当前日期或整数计算文件名。
 *
 */
public class FileNamePattern {

    private Logger logger = LoggerFactory.getLogger(FileNamePattern.class);

    static final Map<String, String> CONVERTER_MAP = new HashMap<String, String>();
    static {
        CONVERTER_MAP.put(IntegerTokenConverter.CONVERTER_KEY, IntegerTokenConverter.class.getName());
        CONVERTER_MAP.put(DateTokenConverter.CONVERTER_KEY, DateTokenConverter.class.getName());
    }

    String pattern;
    Converter<Object> headTokenConverter;

    public FileNamePattern(String patternArg) {
        // 将patternArg字符串中的"\\"替换成"/"
        setPattern(FileFilterUtil.slashify(patternArg));
        parse();
        ConverterUtil.startConverters(this.headTokenConverter);
    }

    void parse() {
        try {
            // 为了进行解析，我们转义了“)”。请注意，原始模式被保留，因为它在状态消息中显示给用户。我们不希望转义版本泄露出去。
            String patternForParsing = escapeRightParantesis(pattern);
            Parser<Object> p = new Parser<Object>(patternForParsing, new AlmostAsIsEscapeUtil());
            Node t = p.parse();
            this.headTokenConverter = p.compile(t, CONVERTER_MAP);

        } catch (ScanException sce) {
            logger.error("无法分析模式“" + pattern + "”。");
            logger.error(sce.getMessage(), sce);
        }
    }

    String escapeRightParantesis(String in) {
        return pattern.replace(")", "\\)");
    }

    public String toString() {
        return pattern;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FileNamePattern other = (FileNamePattern) obj;
        if (pattern == null) {
            if (other.pattern != null)
                return false;
        } else if (!pattern.equals(other.pattern))
            return false;
        return true;
    }

    public IntegerTokenConverter getIntegerTokenConverter() {
        Converter<Object> p = headTokenConverter;

        while (p != null) {
            if (p instanceof IntegerTokenConverter) {
                return (IntegerTokenConverter) p;
            }

            p = p.getNext();
        }
        return null;
    }

    public String convert(Object o) {
        StringBuilder buf = new StringBuilder();
        Converter<Object> p = headTokenConverter;
        while (p != null) {
//            System.out.println(p.getClass().getName());
            buf.append(p.convert(o));
            p = p.getNext();
        }
        return buf.toString();
    }

    public String convertInt(int i) {
        return convert(i);
    }

    public void setPattern(String pattern) {
        if (pattern != null) {
            // 模式中的尾随空格被认为是不需要的。
            this.pattern = pattern.trim();
        }
    }

    public String getPattern() {
        return pattern;
    }

    /**
     * 给定日期，将此实例转换为正则表达式
     */
    public String toRegex() {
        StringBuilder buf = new StringBuilder();
        Converter<Object> p = headTokenConverter;
        while (p != null) {
            if (p instanceof LiteralConverter) {
                buf.append(p.convert(null));
            } else if (p instanceof IntegerTokenConverter) {
                buf.append("\\d{1,2}");
            } else if (p instanceof DateTokenConverter) {
                DateTokenConverter<Object> dtc = (DateTokenConverter<Object>) p;
                buf.append(dtc.toRegex());
            }
            p = p.getNext();
        }
        return buf.toString();
    }
}
