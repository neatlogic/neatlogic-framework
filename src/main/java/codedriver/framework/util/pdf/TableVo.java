/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */
package codedriver.framework.util.pdf;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author longrf
 * @date 2022/10/28 12:14
 */

public class TableVo {

    //字体
    FontVo fontVo;
    //每行的单元格个数
    int cellNum;
    // 将表格分成几等份，new PdfPTable必填入参
    int numColumns;
    // 表格宽度 默认是80.0F
    float widthPercentage;
    //每个单元格高度
    int cellHeight;
    //单元格内容水平位置
    int horizontalAlignment = 1;
    //单元格内容垂直位置
    int verticalAlignment = 5;
    //表头
    Map<Integer, ParagraphVo> headerMap;
    //数据
    List<Map<Integer, ParagraphVo>> dataList;

    PdfPTable table;

    private BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
    private Font font = new Font(bfChinese, 12, Font.NORMAL);


    public TableVo(FontVo fontVo, int cellNum, int numColumns, Map<Integer, ParagraphVo> headerMap, List<Map<Integer, ParagraphVo>> dataList) throws DocumentException, IOException {
        this.fontVo = fontVo;
        this.cellNum = cellNum;
        this.numColumns = numColumns;
        this.headerMap = headerMap;
        this.dataList = dataList;
    }

    public TableVo(FontVo fontVo, int cellNum, int numColumns, float widthPercentage, Map<Integer, ParagraphVo> headerMap, List<Map<Integer, ParagraphVo>> dataList) throws DocumentException, IOException {
        this.fontVo = fontVo;
        this.cellNum = cellNum;
        this.numColumns = numColumns;
        this.widthPercentage = widthPercentage;
        this.headerMap = headerMap;
        this.dataList = dataList;
    }

    public TableVo(FontVo fontVo, int cellNum, int numColumns, int cellHeight, int horizontalAlignment, int verticalAlignment, Map<Integer, ParagraphVo> headerMap, List<Map<Integer, ParagraphVo>> dataList) throws DocumentException, IOException {
        this.fontVo = fontVo;
        this.cellNum = cellNum;
        this.numColumns = numColumns;
        this.cellHeight = cellHeight;
        this.horizontalAlignment = horizontalAlignment;
        this.verticalAlignment = verticalAlignment;
        this.headerMap = headerMap;
        this.dataList = dataList;
    }

    public TableVo(FontVo fontVo, int cellNum, int numColumns, int cellHeight, int horizontalAlignment, int verticalAlignment, float widthPercentage, Map<Integer, ParagraphVo> headerMap, List<Map<Integer, ParagraphVo>> dataList) throws DocumentException, IOException {
        this.fontVo = fontVo;
        this.cellNum = cellNum;
        this.numColumns = numColumns;
        this.cellHeight = cellHeight;
        this.horizontalAlignment = horizontalAlignment;
        this.verticalAlignment = verticalAlignment;
        this.widthPercentage = widthPercentage;
        this.headerMap = headerMap;
        this.dataList = dataList;
    }

    public FontVo getFontVo() {
        return fontVo;
    }

    public void setFontVo(FontVo fontVo) {
        this.fontVo = fontVo;
    }

    public int getCellNum() {
        return cellNum;
    }

    public void setCellNum(int cellNum) {
        this.cellNum = cellNum;
    }

    public int getNumColumns() {
        return numColumns;
    }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
    }

    public float getWidthPercentage() {
        return widthPercentage;
    }

    public void setWidthPercentage(float widthPercentage) {
        this.widthPercentage = widthPercentage;
    }

    public int getCellHeight() {
        return cellHeight;
    }

    public void setCellHeight(int cellHeight) {
        this.cellHeight = cellHeight;
    }

    public int getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public void setHorizontalAlignment(int horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    public int getVerticalAlignment() {
        return verticalAlignment;
    }

    public void setVerticalAlignment(int verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    public Map<Integer, ParagraphVo> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(Map<Integer, ParagraphVo> headerMap) {
        this.headerMap = headerMap;
    }

    public List<Map<Integer, ParagraphVo>> getDataList() {
        return dataList;
    }

    public void setDataList(List<Map<Integer, ParagraphVo>> dataList) {
        this.dataList = dataList;
    }

    public PdfPTable getTable() throws DocumentException, IOException {
        if (MapUtils.isEmpty(headerMap) || CollectionUtils.isEmpty(dataList)) {
            return null;
        }
        this.table = new PdfPTable(getNumColumns());
        if (getWidthPercentage() > 0) {
            table.setWidthPercentage(getWidthPercentage());
        }
        addCell(headerMap);
        for (Map<Integer, ParagraphVo> dataMap : dataList) {
            addCell(dataMap);
        }
        return table;
    }

    void addCell(Map<Integer, ParagraphVo> map) throws DocumentException, IOException {
        for (int i = 0; i < cellNum; i++) {
            PdfPCell cell = new PdfPCell(map.get(i + 1).getParagraph());
            //设置单元格的宽度
            cell.setColspan(table.getNumberOfColumns() / cellNum);
            // 设置内容水平位置，默认居中显示
            cell.setHorizontalAlignment(horizontalAlignment != 1 ? horizontalAlignment : 1);
            // 设置垂直位置，默认居中显示
            cell.setVerticalAlignment(verticalAlignment != 5 ? verticalAlignment : 5);
            //设置单元格的高度
            if (cellHeight > 0) {
                cell.setFixedHeight(cellHeight);
            }
            table.addCell(cell);
        }
    }
}
