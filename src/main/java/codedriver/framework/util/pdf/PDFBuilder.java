/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */
package codedriver.framework.util.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
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
     * open之前可以设置页面排版，open才可以添加文档内容
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
         * @param paragraphVo 段落
         * @return Builder
         * @throws DocumentException e
         */
        public Builder addParagraph(ParagraphVo paragraphVo) throws DocumentException, IOException {
            if (paragraphVo == null || paragraphVo.getParagraph() == null) {
                return this;
            }
            document.add(paragraphVo.getParagraph());
            return this;
        }

        /**
         * 添加表格
         *
         * @param tableVo tableVo
         * @param isMerge 是否需要合并
         * @return Builder
         * @throws DocumentException e
         * @throws IOException       e
         */
        public Builder addTable(TableVo tableVo, boolean isMerge) throws DocumentException, IOException {
            if (!isMerge) {
                //避免与上面段落重叠，添加表格前增加空白位置
                Paragraph paragraph = new Paragraph();
                paragraph.setSpacingAfter(5);
                document.add(paragraph);
            }
            //添加表格
            document.add(tableVo.getTable());
            return this;
        }

        public Document builder() {
            return document;
        }

        /**
         * 关闭文档
         */
        public void close() {
            document.close();
        }
    }

    public Document builder() {
        return document;
    }
}
