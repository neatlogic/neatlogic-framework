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
package neatlogic.framework.util.word;

import neatlogic.framework.util.word.enums.ParagraphAlignmentType;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

/**
 * @author longrf
 * @date 2022/9/26 16:39
 */

public class ParagraphBuilder {

    XWPFParagraph paragraph;
    XWPFRun run;

    public ParagraphBuilder(XWPFParagraph paragraph) {
        this.paragraph = paragraph;
    }


    public ParagraphBuilder(XWPFParagraph paragraph ,String paragraphText) {
        this.paragraph = paragraph;
        run = paragraph.createRun();
        run.setText(paragraphText);
    }

    public XWPFParagraph builder() {
        return paragraph;
    }

    /**
     * 设置段落内容
     *
     * @param context 段落内容
     * @return ParagraphBuilder
     */
    public ParagraphBuilder setText(String context) {
        if (run == null) {
            run = paragraph.createRun();
        }
        run.setText(context);
        return this;
    }

    /**
     * 粗体设置
     *
     * @param bold 是否粗体 默认不粗体
     * @return ParagraphBuilder
     */
    public ParagraphBuilder setBold(Boolean bold) {
        if (run == null) {
            run = paragraph.createRun();
        }
        run.setBold(bold);
        return this;
    }

    /**
     * 颜色设置
     *
     * @param color 颜色（16进制） 默认白色
     * @return ParagraphBuilder
     */
    public ParagraphBuilder setColor(String color) {
        if (run == null) {
            run = paragraph.createRun();
        }
        run.setColor(color);
        return this;
    }

    /**
     * 字体设置
     *
     * @param fontFamily 字体  默认宋体
     * @return ParagraphBuilder
     */
    public ParagraphBuilder setFontFamily(String fontFamily) {
        if (run == null) {
            run = paragraph.createRun();
        }
        run.setFontFamily(fontFamily);
        return this;
    }

    /**
     * 字号设置
     *
     * @param fontSize 字号  默认10.5
     * @return ParagraphBuilder
     */
    public ParagraphBuilder setFontSize(Integer fontSize) {
        if (run == null) {
            run = paragraph.createRun();
        }
        run.setFontSize(fontSize);
        return this;
    }

    /**
     * 行间距设置
     *
     * @param textPosition 行间距  默认是1.15倍
     * @return ParagraphBuilder
     */
    public ParagraphBuilder setTextPosition(Integer textPosition) {
        if (run == null) {
            run = paragraph.createRun();
        }
        run.setTextPosition(textPosition);
        return this;
    }

    /**
     * 首行是否缩进设置
     *
     * @param indentationFirstLine 首行是否缩进（567:首行缩进）  默认不缩进
     * @return ParagraphBuilder
     */
    public ParagraphBuilder setIndentationFirstLine(Boolean indentationFirstLine) {
        if (run == null) {
            run = paragraph.createRun();
        }
        paragraph.setIndentationFirstLine(indentationFirstLine ? 567 : 1);
        return this;
    }

    /**
     * 字体对齐方式设置
     *
     * @param alignmentType 字体对齐方式：1左对齐 2居中 3右对齐  默认左对齐
     * @return ParagraphBuilder
     */
    public ParagraphBuilder setAlignmentType(ParagraphAlignmentType alignmentType) {
        if (run == null) {
            run = paragraph.createRun();
        }
        paragraph.setFontAlignment(alignmentType.getValue());
        return this;
    }

    /**
     * 个性化设置
     *
     * @param bold                 是否粗体 默认不粗体
     * @param color                颜色（16进制） 默认白色
     * @param fontFamily           字体  默认宋体
     * @param fontSize             字号  默认10.5
     * @param textPosition         行间距  默认是1.15倍
     * @param indentationFirstLine 首行是否缩进（567:首行缩进）  默认不缩进
     * @param alignmentType        字体对齐方式：1左对齐 2居中 3右对齐  默认左对齐
     * @return ParagraphBuilder
     */
    public ParagraphBuilder setCustom(Boolean bold, String color, String fontFamily, Integer fontSize, Integer textPosition, Boolean indentationFirstLine, ParagraphAlignmentType alignmentType) {
        if (run == null) {
            run = paragraph.createRun();
        }
        run.setBold(bold);
        run.setColor(color);
        run.setFontFamily(fontFamily);
        run.setFontSize(fontSize);
        run.setTextPosition(textPosition);
        paragraph.setIndentationFirstLine(indentationFirstLine ? 567 : 1);
        paragraph.setFontAlignment(alignmentType.getValue());
        return this;
    }

}
