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
package neatlogic.framework.util.pdf;

import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

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
