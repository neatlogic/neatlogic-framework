/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package neatlogic.framework.util.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;

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

        /**
         * 添加章节
         * @param chapter
         * @return
         * @throws DocumentException
         */
        public Builder addCharter(Chapter chapter) throws DocumentException {
            document.add(chapter);
            return this;
        }

        /**
         * 重新开始一页
         * @return
         */
        public Builder newPage() {
            document.newPage();
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
