/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */
package codedriver.framework.file.core.encoder;

import codedriver.framework.file.core.layout.Layout;

public class PatternLayoutEncoderBase<E> extends LayoutWrappingEncoder<E> {

    @Override
    public void setLayout(Layout<E> layout) {
        throw new UnsupportedOperationException("one cannot set the layout of " + this.getClass().getName());
    }

}
