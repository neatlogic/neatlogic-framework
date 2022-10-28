/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */
package codedriver.framework.util.pdf;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * @author longrf
 * @date 2022/10/28 10:08
 */

public class ParagraphVo {

    //内容
    String text;
    //字体
    FontVo fontVo;
    //对齐方式
    int alignment;
    //首行缩进
    float firstLineIndent;
    //段左缩进
    float indentationLeft;
    //段右缩进
    float indentationRight;
    //段前间距
    float spacingBefore;
    //段后间距
    float spacingAfter;

    Paragraph paragraph;

    //字体规范
    private BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
    private Font font = new Font(bfChinese, 12, Font.NORMAL);

    public ParagraphVo() throws DocumentException, IOException {
    }

    public ParagraphVo(String text) throws DocumentException, IOException {
        this.text = text;
    }

    public ParagraphVo(String text, FontVo fontVo) throws DocumentException, IOException {
        this.text = text;
        this.fontVo = fontVo;
    }

    public ParagraphVo(String text, FontVo fontVo, int alignment) throws DocumentException, IOException {
        this.text = text;
        this.fontVo = fontVo;
        this.alignment = alignment;
    }

    public ParagraphVo(String text, FontVo fontVo, int alignment, float firstLineIndent, float indentationLeft, float indentationRight, float spacingBefore, float spacingAfter) throws DocumentException, IOException {
        this.text = text;
        this.fontVo = fontVo;
        this.alignment = alignment;
        this.firstLineIndent = firstLineIndent;
        this.indentationLeft = indentationLeft;
        this.indentationRight = indentationRight;
        this.spacingBefore = spacingBefore;
        this.spacingAfter = spacingAfter;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public FontVo getFontVo() {
        return fontVo;
    }

    public void setFontVo(FontVo fontVo) {
        this.fontVo = fontVo;
    }

    public int getAlignment() {
        return alignment;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public float getFirstLineIndent() {
        return firstLineIndent;
    }

    public void setFirstLineIndent(float firstLineIndent) {
        this.firstLineIndent = firstLineIndent;
    }

    public float getIndentationLeft() {
        return indentationLeft;
    }

    public void setIndentationLeft(float indentationLeft) {
        this.indentationLeft = indentationLeft;
    }

    public float getIndentationRight() {
        return indentationRight;
    }

    public void setIndentationRight(float indentationRight) {
        this.indentationRight = indentationRight;
    }

    public float getSpacingBefore() {
        return spacingBefore;
    }

    public void setSpacingBefore(float spacingBefore) {
        this.spacingBefore = spacingBefore;
    }

    public float getSpacingAfter() {
        return spacingAfter;
    }

    public void setSpacingAfter(float spacingAfter) {
        this.spacingAfter = spacingAfter;
    }


    public Paragraph getParagraph() throws DocumentException, IOException {
        if (paragraph != null) {
            return paragraph;
        }
        if (StringUtils.isBlank(getText())) {
            return null;
        }
        //段落
        paragraph = new Paragraph(getText(), getFontVo() != null ? getFontVo().getFont() : font);
        //对齐方式
        if (getAlignment() > 0) {
            paragraph.setAlignment(getAlignment());
        }
        //首行缩进
        if (getFirstLineIndent() > 0) {
            paragraph.setFirstLineIndent(getFirstLineIndent());
        }
        //段左缩进
        if (getIndentationLeft() > 0) {
            paragraph.setIndentationLeft(getIndentationLeft());
        }
        //段右缩进
        if (getIndentationRight() > 0) {
            paragraph.setIndentationRight(getIndentationRight());
        }
        //段前间距
        if (getSpacingBefore() > 0) {
            paragraph.setSpacingBefore(getSpacingBefore());
        }
        //段后间距
        if (getIndentationRight() > 0) {
            paragraph.setIndentationRight(getIndentationRight());
        }
        return paragraph;
    }

    public void setParagraph(Paragraph paragraph) {
        this.paragraph = paragraph;
    }
}
