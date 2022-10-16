/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */
package codedriver.framework.file.core.layout;

import ch.qos.logback.core.spi.ScanException;
import codedriver.framework.file.core.pattern.Converter;
import codedriver.framework.file.core.pattern.parser.Node;
import codedriver.framework.file.core.pattern.parser.Parser;
import codedriver.framework.file.core.pattern.PostCompileProcessor;
import codedriver.framework.file.core.util.ConverterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

abstract public class PatternLayoutBase<E> extends LayoutBase<E> {

    private Logger logger = LoggerFactory.getLogger(PatternLayoutBase.class);
    static final int INTIAL_STRING_BUILDER_SIZE = 256;
    Converter<E> head;
    String pattern;
    protected PostCompileProcessor<E> postCompileProcessor;

    Map<String, String> instanceConverterMap = new HashMap<String, String>();

    /**
     * 此类的具体实现负责详细说明模式字和转换器之间的映射。
     *
     * @return 将模式字与转换器类的名称相关联的映射
     */
    abstract public Map<String, String> getDefaultConverterMap();

    /**
     * 返回默认转换器映射与上下文中包含的映射合并的映射。
     */
    public Map<String, String> getEffectiveConverterMap() {
        Map<String, String> effectiveMap = new HashMap<String, String>();

        // add the least specific map fist
        Map<String, String> defaultMap = getDefaultConverterMap();
        if (defaultMap != null) {
            effectiveMap.putAll(defaultMap);
        }

        // set the most specific map last
        effectiveMap.putAll(instanceConverterMap);
        return effectiveMap;
    }

    public void start() {
        if (pattern == null || pattern.length() == 0) {
            logger.error("模式为空。");
            return;
        }
        try {
            Parser<E> p = new Parser<E>(pattern);
            Node t = p.parse();
            this.head = p.compile(t, getEffectiveConverterMap());
            if (postCompileProcessor != null) {
                postCompileProcessor.process(head);
            }
            ConverterUtil.startConverters(this.head);
            super.start();
        } catch (ScanException sce) {
            logger.error("无法分析模式[" + getPattern() + "]");
            logger.error(sce.getMessage(), sce);
        }
    }

    public void setPostCompileProcessor(PostCompileProcessor<E> postCompileProcessor) {
        this.postCompileProcessor = postCompileProcessor;
    }

    protected String writeLoopOnConverters(E event) {
//        System.out.println("b3");
        StringBuilder strBuilder = new StringBuilder(INTIAL_STRING_BUILDER_SIZE);
        Converter<E> c = head;
        while (c != null) {
            c.write(strBuilder, event);
            c = c.getNext();
        }
        return strBuilder.toString();
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String toString() {
        return this.getClass().getName() + "(\"" + getPattern() + "\")";
    }

    public Map<String, String> getInstanceConverterMap() {
        return instanceConverterMap;
    }

}
