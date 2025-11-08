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

import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

/**
 * @author longrf
 * @date 2022/10/26 10:10
 */

public class TableBuilder {

    private PdfPTable table;
    //每行的单元格个数
    private int cellNum;
    //每个单元格高度
    private int cellHeight;
    //单元格内容水平位置
    private int horizontalAlignment = 1;
    //单元格内容垂直位置
    private int verticalAlignment = 1;

    /**
     * @param cellNum 一行的单元格个数
     */
    public TableBuilder(int cellNum) {
        //new PdfPTable必填入参， 将表格分成几等份
        this.table = new PdfPTable(cellNum);
        this.cellNum = cellNum;
        this.table.setSpacingBefore(5);
    }

    public TableBuilder addCell(Paragraph paragraph) {
        PdfPCell cell = new PdfPCell(paragraph);

        //设置单元格的宽度，份数为单位
        cell.setColspan(table.getNumberOfColumns() / cellNum);
        // 设置内容水平位置，默认居中显示
        cell.setHorizontalAlignment(horizontalAlignment != 1 ? horizontalAlignment : 1);
        // 设置垂直位置，默认居中显示
        cell.setVerticalAlignment(verticalAlignment != 1 ? verticalAlignment : 1);

        //设置单元格的高度
        if (cellHeight > 0) {
            cell.setFixedHeight(cellHeight);
        }
        table.addCell(cell);
        return this;
    }

    /**
     * 设置单元格的高度
     *
     * @param cellHeight 高度
     * @return TableBuilder
     */
    public TableBuilder setFixedHeight(int cellHeight) {
        this.cellHeight = cellHeight;
        return this;
    }

    /**
     * 设置内容水平位置 Element入参
     *
     * @param horizontalAlignment Element入参
     * @return TableBuilder
     */
    public TableBuilder setHorizontalAlignment(int horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
        return this;
    }

    /**
     * 设置内容垂直位置
     *
     * @param verticalAlignment Element入参
     * @return TableBuilder
     */
    public TableBuilder setVerticalAlignment(int verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
        return this;
    }

    /**
     * 设置表格宽度
     *
     * @param widthPercentage 表格宽度 默认是80.0F
     * @return TableBuilder
     */
    public TableBuilder setWidthPercentage(float widthPercentage) {
        table.setWidthPercentage(widthPercentage);
        return this;
    }

    /**
     * 设置表格margin
     * @param spacingBefore
     * @return
     */
    public TableBuilder setSpacingBefore(float spacingBefore) {
        table.setSpacingBefore(spacingBefore);
        return this;
    }

    public PdfPTable builder() {
        return table;
    }
}
