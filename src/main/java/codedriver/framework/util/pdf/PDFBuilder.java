/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */
package codedriver.framework.util.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPTable;
import com.lowagie.text.PageSize;

import java.io.IOException;

/**
 * @author longrf
 * @date 2022/10/20 16:00
 */

public class PDFBuilder {

    private Document document;
    public PDFBuilder() throws IOException {
        this.document = new Document();
    }

    /**
     * 设置页面横版
     *
     * @return PDFBuilder
     */
    public PDFBuilder setPageSizeHorizontal() {
        Rectangle pageSize = new Rectangle(PageSize.A4.getHeight(), PageSize.A4.getWidth());
        pageSize.rotate();
        document.setPageSize(pageSize);
        return this;
    }

    /**
     * 设置页面竖版
     *
     * @return PDFBuilder
     */
    public PDFBuilder setPageSizeVertical() {
        Rectangle pageSize = new Rectangle(PageSize.A4.getWidth(), PageSize.A4.getHeight());
        pageSize.rotate();
        document.setPageSize(pageSize);
        return this;
    }

    /**
     * 设置页面竖版
     *
     * @return PDFBuilder
     */
    public PDFBuilder setMargins(float marginLeft, float marginRight, float marginTop, float marginBottom) {
        document.setMargins(marginLeft, marginRight, marginTop, marginBottom);
        return this;
    }

    /**
     * 打开文档
     *
     * @return PDFBuilder
     */
    public Builder open() {
        document.open();
        return new Builder();
    }

    public class Builder {

        /**
         * 添加段落
         *
         * @param paragraph 段落
         * @return Builder
         * @throws DocumentException e
         */
        public Builder addParagraph(Paragraph paragraph) throws DocumentException {
            document.add(paragraph);
            return this;
        }


        /**
         * 添加段落
         *
         * @param text 段落内容
         * @return Builder
         * @throws DocumentException e
         */
        public Builder addParagraph(String text) throws DocumentException {
            document.add(new Paragraph(text));
            return this;
        }

        /**
         * 添加表格
         *
         * @param table 表格
         * @return Builder
         * @throws DocumentException e
         */
        public Builder addTable(PdfPTable table) throws DocumentException {

            //避免与上面段落重叠，添加表格前增加空白位置
            Paragraph paragraph = new Paragraph();
            paragraph.setSpacingAfter(5);
            document.add(paragraph);

            //添加表格
            document.add(table);
            return this;
        }

        public Document builder() {
            return document;
        }

        public void close() {
            document.close();
        }
    }

    public Document builder() {
        return document;
    }
}
