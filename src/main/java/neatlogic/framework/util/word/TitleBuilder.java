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
import neatlogic.framework.util.word.enums.TitleType;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.math.BigInteger;

/**
 * @author longrf
 * @date 2022/9/30 16:46
 */

public class TitleBuilder {

    XWPFParagraph paragraph;
    XWPFRun run;

    /**
     * 创建TitleBuilder
     *
     * @param document  文件
     * @param titleType 标题类型
     * @param titleName 标题内容
     */
    public TitleBuilder(XWPFDocument document, TitleType titleType, String titleName) {
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        //设定默认行间距
        run.setTextPosition(35);
        //加粗
        run.setBold(titleType.getBold());
        //设置颜色--十六进制
        run.setColor(titleType.getColor());
        //字体
        run.setFontFamily(titleType.getFontFamily());
        //字体大小
        run.setFontSize(titleType.getFontSize());
        //标题内容
        run.setText(titleName);

        if (titleType.getValue().equals(TitleType.TILE.getValue())) {
            //对齐方式
            paragraph.setAlignment(ParagraphAlignment.CENTER);
        } else if (titleType.getValue().equals(TitleType.H1.getValue()) || titleType.getValue().equals(TitleType.H2.getValue()) || titleType.getValue().equals(TitleType.H3.getValue())) {
            addCustomHeadingStyle(document, titleType.getText(), titleType.getHeadingLevel());
            paragraph.setStyle(titleType.getText());
        }
    }

    /**
     * 粗体设置
     *
     * @param bold 是否粗体 默认粗体
     * @return TitleBuilder
     */
    public TitleBuilder setBold(Boolean bold) {
        run.setBold(bold);
        return this;
    }

    /**
     * 颜色设置
     *
     * @param color 颜色（16进制） 默认白色
     * @return TitleBuilder
     */
    public TitleBuilder setColor(String color) {
        run.setColor(color);
        return this;
    }

    /**
     * 字体设置
     *
     * @param fontFamily 字体 默认楷体
     * @return TitleBuilder
     */
    public TitleBuilder setFontFamily(String fontFamily) {
        run.setFontFamily(fontFamily);
        return this;
    }

    /**
     * 字号设置
     *
     * @param fontSize 字号  标题1默认18， 标题2默认15， 标题3默认14，标题默认15
     * @return TitleBuilder
     */
    public TitleBuilder setFontSize(Integer fontSize) {
        run.setFontSize(fontSize);
        return this;
    }

    /**
     * 行间距设置
     *
     * @param textPosition 行间距  默认是35
     * @return TitleBuilder
     */
    public TitleBuilder setTextPosition(Integer textPosition) {
        run.setTextPosition(textPosition);
        return this;
    }

    /**
     * 字体对齐方式设置
     *
     * @param alignmentType 字体对齐方式：1左对齐 2居中 3右对齐  默认左对齐
     * @return ParagraphBuilder
     */
    public TitleBuilder setAlignmentType(ParagraphAlignmentType alignmentType) {
        if (run == null) {
            run = paragraph.createRun();
        }
        paragraph.setFontAlignment(alignmentType.getValue());
        return this;
    }

    /**
     * 增加自定义标题样式。这里用的是stackoverflow的源码
     *
     * @param docxDocument 目标文档
     * @param strStyleId   样式名称
     * @param headingLevel 样式级别
     */
    private static void addCustomHeadingStyle(XWPFDocument docxDocument, String strStyleId, int headingLevel) {

        CTStyle ctStyle = CTStyle.Factory.newInstance();
        ctStyle.setStyleId(strStyleId);

        CTString styleName = CTString.Factory.newInstance();
        styleName.setVal(strStyleId);
        ctStyle.setName(styleName);

        CTDecimalNumber indentNumber = CTDecimalNumber.Factory.newInstance();
        indentNumber.setVal(BigInteger.valueOf(headingLevel));

        // 较低的数字>样式在格式栏中更为突出
        ctStyle.setUiPriority(indentNumber);
        CTOnOff ct = CTOnOff.Factory.newInstance();
        ctStyle.setUnhideWhenUsed(ct);

        // 样式显示在格式栏中
        ctStyle.setQFormat(ct);

        // 样式定义了给定级别的标题
        CTPPr ppr = CTPPr.Factory.newInstance();
        ppr.setOutlineLvl(indentNumber);
        ctStyle.setPPr(ppr);

        // 如果已定义，则为空操作
        XWPFStyle style = new XWPFStyle(ctStyle);
        XWPFStyles styles = docxDocument.createStyles();
        style.setType(STStyleType.PARAGRAPH);
        styles.addStyle(style);

    }
}
