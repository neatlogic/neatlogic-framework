/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */
package codedriver.framework.util.pdf;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;

import java.io.IOException;

/**
 * @author longrf
 * @date 2022/10/28 10:13
 */

public class FontVo {

    //字体规范
    private BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
    //字体
    private Font font;

    public FontVo() throws DocumentException, IOException {
    }

    public FontVo(int fontSize) throws DocumentException, IOException {
        font = new Font(bfChinese, fontSize, Font.NORMAL);
    }

    public FontVo(int fontSize, boolean isBold) throws DocumentException, IOException {
        font = new Font(bfChinese, fontSize, isBold ? Font.BOLD : Font.NORMAL);
    }

    public FontVo(int fontSize, boolean isBold, BaseColor color) throws DocumentException, IOException {
        font = new Font(bfChinese, fontSize, isBold ? Font.BOLD : Font.NORMAL, color);
    }

    public Font getFont() {
        if (font == null) {
            font = new Font(bfChinese, 12, Font.NORMAL);
        }
        return font;

    }

    public void setFont(Font font) {
        this.font = font;
    }
}
