/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */
package codedriver.framework.util.pdf;

import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import java.io.IOException;

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
     * @param numColumns new PdfPTable必填入参， 将表格分成几等份
     * @param cellNum    一行的单元格个数
     */
    public TableBuilder(int numColumns, int cellNum) {
        this.table = new PdfPTable(numColumns);
        this.cellNum = cellNum;
    }

    public TableBuilder addCell(Paragraph paragraph) throws IOException {
        PdfPCell cell = new PdfPCell(paragraph);

        //设置单元格的宽度
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

    public PdfPTable builder() {
        return table;
    }

}
