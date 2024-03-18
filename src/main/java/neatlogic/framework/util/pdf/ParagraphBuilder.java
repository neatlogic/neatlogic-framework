/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

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
package neatlogic.framework.util.pdf;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;

import java.io.IOException;

/**
 * @author longrf
 * @date 2022/10/21 16:13
 */

public class ParagraphBuilder {

    private Paragraph paragraph;
    //字体规范
    private BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
    private Font font = new Font(bfChinese, 12, Font.NORMAL);

    public ParagraphBuilder() throws DocumentException, IOException {
        paragraph = new Paragraph();
        paragraph.setFont(font);
    }

    public ParagraphBuilder(String text) throws DocumentException, IOException {
        paragraph = new Paragraph(text);
        paragraph.setFont(font);
    }

    public ParagraphBuilder(String text, Font font) throws DocumentException, IOException {
        paragraph = new Paragraph(text, font);
        paragraph.setFont(font);
    }

    /**
     * 添加文本
     *
     * @param text 文本
     * @return ParagraphBuilder
     */
    public ParagraphBuilder addText(String text) {
        paragraph.add(text);
        return this;
    }

    /**
     * 设置字体
     *
     * @param font 字体
     * @return ParagraphBuilder
     */
    public ParagraphBuilder setFont(Font font) {
        paragraph.setFont(font);
        return this;
    }

    /**
     * 设置首行缩进
     *
     * @param indentation 缩进单位
     * @return ParagraphBuilder
     */
    public ParagraphBuilder setFirstLineIndent(float indentation) {
        paragraph.setFirstLineIndent(indentation);
        return this;
    }

    /**
     * 设置段左缩进
     *
     * @param indentation 缩进单位
     * @return ParagraphBuilder
     */
    public ParagraphBuilder setIndentationLeft(float indentation) {
        paragraph.setIndentationLeft(indentation);
        return this;
    }

    /**
     * 设置段右缩进
     *
     * @param indentation 缩进单位
     * @return ParagraphBuilder
     */
    public ParagraphBuilder setIndentationRight(float indentation) {
        paragraph.setIndentationRight(indentation);
        return this;
    }

    /**
     * 设置段前间距
     *
     * @param indentation 间距单位
     * @return ParagraphBuilder
     */
    public ParagraphBuilder setSpacingBefore(float indentation) {
        paragraph.setSpacingBefore(indentation);
        return this;
    }

    /**
     * 设置段后间距
     *
     * @param indentation 间距单位
     * @return ParagraphBuilder
     */
    public ParagraphBuilder setSpacingAfter(float indentation) {
        paragraph.setSpacingAfter(indentation);
        return this;
    }

    /**
     * 设置对齐方式  入参枚举：Element
     *
     * @param alignment 间距单位
     * @return ParagraphBuilder
     */
    public ParagraphBuilder setAlignment(int alignment) {
        paragraph.setAlignment(alignment);
        return this;
    }

    public Paragraph builder() throws DocumentException, IOException {
        //默认字体，不设置字体会导出空文档
        if (paragraph.getFont() == null) {
            BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            paragraph.setFont(new Font(bfChinese, 12, Font.NORMAL));
        }
        return paragraph;
    }
}
