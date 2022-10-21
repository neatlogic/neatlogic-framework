/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */
package codedriver.framework.util.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.lowagie.text.PageSize;

import java.io.IOException;

/**
 * @author longrf
 * @date 2022/10/20 16:00
 */

public class PDFBuilder {

    private Document document;
    BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

    Font font = new Font(bfChinese, 12, Font.NORMAL);


    public PDFBuilder() throws DocumentException, IOException {
        this.document = new Document();
        document.open();
    }

    /**
     * 设置页面横版
     * @return PDFBuilder
     */
    public PDFBuilder setPageSizeHorizontal() {
        //竖向
        Rectangle pageSize = new Rectangle(PageSize.A4.getHeight(), PageSize.A4.getWidth());
        pageSize.rotate();
        document.setPageSize(pageSize);
        return this;
    }

    /**
     * 设置页面竖版
     * @return PDFBuilder
     */
    public PDFBuilder setPageSizeVertical() {
        //横向
        Rectangle pageSize = new Rectangle(PageSize.A4.getWidth(), PageSize.A4.getHeight());
        pageSize.rotate();
        document.setPageSize(pageSize);
        return this;
    }

    /**
     * 设置字体大小
     * @param size 大小
     * @return PDFBuilder
     */
    public PDFBuilder setFontSize(int size) {
        font.setSize(size);
        return this;
    }

    /**
     * 设置字体颜色
     * @param color 颜色
     * @return PDFBuilder
     */
    public PDFBuilder setFontColor( BaseColor color) {
        font.setColor(color);
        return this;
    }



}
