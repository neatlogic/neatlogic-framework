/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */
package codedriver.framework.util.pdf;

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

        //
        return paragraph;
    }
}
