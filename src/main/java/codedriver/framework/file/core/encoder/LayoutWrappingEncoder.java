/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.file.core.encoder;

import codedriver.framework.file.core.IEvent;
import codedriver.framework.file.core.layout.Layout;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class LayoutWrappingEncoder<E> extends EncoderBase<E> {

    protected Layout<E> layout;

    /**
     * 将字符串转换为字节时要使用的字符集。
     */
    private Charset charset = StandardCharsets.UTF_8;

    public Layout<E> getLayout() {
        return layout;
    }

    public void setLayout(Layout<E> layout) {
        this.layout = layout;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    private byte[] convertToBytes(String s) {
        if (charset == null) {
            return s.getBytes();
        } else {
            return s.getBytes(charset);
        }
    }

    public byte[] encode(E e) {
//        System.out.println("b1");
        String txt = layout.doLayout(e);//PatternLayout
        if (e instanceof IEvent) {
            ((IEvent) e).setFinalMessage(txt);
        }
        return convertToBytes(txt);
    }

    public boolean isStarted() {
        return false;
    }

    public void start() {
        started = true;
    }

    public void stop() {
        started = false;
    }

}
