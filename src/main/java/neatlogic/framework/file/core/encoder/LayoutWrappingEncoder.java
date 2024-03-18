/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

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
