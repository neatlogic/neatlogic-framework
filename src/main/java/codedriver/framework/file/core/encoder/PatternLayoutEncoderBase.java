/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */
package codedriver.framework.file.core.encoder;

import codedriver.framework.file.core.layout.Layout;

public class PatternLayoutEncoderBase<E> extends LayoutWrappingEncoder<E> {

    String pattern;

    // 由于流行的需求outputPatternAsHeader默认设置为false
//    protected boolean outputPatternAsHeader = false;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

//    public boolean isOutputPatternAsHeader() {
//        return outputPatternAsHeader;
//    }

    /**
     * 在日志文件中将模式字符串打印为标题
     *
     * @param outputPatternAsHeader
     * @since 1.0.3
     */
//    public void setOutputPatternAsHeader(boolean outputPatternAsHeader) {
//        this.outputPatternAsHeader = outputPatternAsHeader;
//    }

//    public boolean isOutputPatternAsPresentationHeader() {
//        return outputPatternAsHeader;
//    }

    /**
     * @deprecated replaced by {@link #setOutputPatternAsHeader(boolean)}
     */
//    public void setOutputPatternAsPresentationHeader(boolean outputPatternAsHeader) {
////        addWarn("[outputPatternAsPresentationHeader] property is deprecated. Please use [outputPatternAsHeader] option instead.");
//        this.outputPatternAsHeader = outputPatternAsHeader;
//    }

    @Override
    public void setLayout(Layout<E> layout) {
        throw new UnsupportedOperationException("one cannot set the layout of " + this.getClass().getName());
    }

}
