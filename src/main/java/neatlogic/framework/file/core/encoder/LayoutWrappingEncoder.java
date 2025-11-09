/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.file.core.encoder;

import ch.qos.logback.core.CoreConstants;
import neatlogic.framework.file.core.layout.Layout;

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

    @Override
    public byte[] headerBytes() {
        if (layout == null)
            return null;

        StringBuilder sb = new StringBuilder();
        appendIfNotNull(sb, layout.getFileHeader());
        if (sb.length() > 0) {
            sb.append(CoreConstants.LINE_SEPARATOR);
        }
        return convertToBytes(sb.toString());
    }

    @Override
    public byte[] footerBytes() {
        if (layout == null)
            return null;

        StringBuilder sb = new StringBuilder();
        appendIfNotNull(sb, layout.getFileFooter());
        return convertToBytes(sb.toString());
    }

    private void appendIfNotNull(StringBuilder sb, String s) {
        if (s != null) {
            sb.append(s);
        }
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
