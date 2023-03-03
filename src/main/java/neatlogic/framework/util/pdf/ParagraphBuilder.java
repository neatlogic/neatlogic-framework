/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
