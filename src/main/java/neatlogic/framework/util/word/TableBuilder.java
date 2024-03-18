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

import neatlogic.framework.util.word.enums.FontFamily;
import neatlogic.framework.util.word.enums.TableColor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author longrf
 * @date 2022/9/23 17:21
 */

public class TableBuilder {

    XWPFTable table;

    private Map<Integer, String> tableHeaderMap;

    String fontFamily;

    double spacingBetween;

    int spacingBeforeLines;

    int spacingAfterLines;

    /**
     * 不含表头建表
     *
     * @param table table
     */
    public TableBuilder(XWPFTable table) {
        this.table = table;
        //表风格
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        tblPr.getTblW().setType(STTblWidth.DXA);
        table.setWidthType(TableWidthType.DXA);

        //默认表格宽度
        tblPr.getTblW().setW(new BigInteger("8310"));
    }

    /**
     * 含有表头建表
     *
     * @param table          table
     * @param tableHeaderMap 表头 1->列1 , 2->列2 , 3->列3
     */
    public TableBuilder(XWPFTable table, Map<Integer, String> tableHeaderMap) {
        tableBuilder(table, tableHeaderMap, null);
    }

    /**
     * 含有表头建表
     *
     * @param table          table
     * @param tableHeaderMap 表头 1->列1 , 2->列2 , 3->列3
     * @param tableColor     表头颜色
     */
    public TableBuilder(XWPFTable table, Map<Integer, String> tableHeaderMap, TableColor tableColor) {
        tableBuilder(table, tableHeaderMap, tableColor);
    }

    /**
     * 含有表头建表
     *
     * @param table          table
     * @param tableHeaderMap 表头 1->列1 , 2->列2 , 3->列3
     * @param tableColor     表头颜色
     */
    void tableBuilder(XWPFTable table, Map<Integer, String> tableHeaderMap, TableColor tableColor) {
        this.table = table;
        //表风格
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        tblPr.getTblW().setType(STTblWidth.DXA);
        table.setWidthType(TableWidthType.DXA);

        //默认表格宽度
        tblPr.getTblW().setW(new BigInteger("8310"));

        this.tableHeaderMap = tableHeaderMap;
        XWPFTableRow row = table.getRow(0);
        for (int cellNum = 1; cellNum <= tableHeaderMap.size(); cellNum++) {
            XWPFTableCell cell = cellNum == 1 ? row.getCell(0) : row.addNewTableCell();
            if (tableColor != null) {
                cell.setColor(tableColor.getValue());
            }
            setCellText(cell, tableHeaderMap.get(cellNum), table.getWidth() / tableHeaderMap.size());
        }
    }

    /**
     * 添加表头
     *
     * @param tableHeaderMap 表头 1->列1 , 2->列2 , 3->列3
     * @return TableBuilder
     */
    public TableBuilder addTableHeaderMap(Map<Integer, String> tableHeaderMap) {
        if (MapUtils.isEmpty(tableHeaderMap)) {
            return this;
        }
        this.tableHeaderMap = tableHeaderMap;

        CTTbl ctTbl = table.getCTTbl();
        CTTblPr tblPr = ctTbl.getTblPr() == null ? ctTbl.addNewTblPr() : ctTbl.getTblPr();
        CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();
        tblWidth.setType(STTblWidth.DXA);

        XWPFTableRow titleRow = table.getRow(0);
        for (int cellNum = 1; cellNum <= tableHeaderMap.size(); cellNum++) {
            XWPFTableCell cell = cellNum == 1 ? titleRow.getCell(0) : titleRow.createCell();
            setCellText(cell, tableHeaderMap.get(cellNum), table.getWidth() / tableHeaderMap.size());
        }

        return this;
    }

    /**
     * 获取表头
     *
     * @return tableHeaderMap
     */
    private Map<Integer, String> getTableHeaderMap() {
        if (MapUtils.isEmpty(tableHeaderMap)) {
            XWPFTableRow row = table.getRows().get(0);
            int i = 1;
            for (XWPFTableCell cell : row.getTableCells()) {
                for (XWPFParagraph paragraph : cell.getParagraphs()) {
                    for (XWPFRun run : paragraph.getRuns()) {
                        tableHeaderMap.put(i, run.toString());
                        i++;
                    }
                }
            }
        }
        return tableHeaderMap;
    }

    /**
     * 添加空行数据
     *
     * @return table
     */
    public TableBuilder addBlankRow() {
        table.createRow();
        return this;
    }

    /**
     * 添加一行数据
     *
     * @param cellMap Map（表头,值）
     * @return TableBuilder
     */
    public TableBuilder addRow(Map<String, String> cellMap) {
        if (MapUtils.isEmpty(cellMap)) {
            return this;
        }
        XWPFTableRow row = table.createRow();
//        row.setHeight(300);
        Map<Integer, String> valueMap = new HashMap<>();
        Map<Integer, String> tableHeaderMap = getTableHeaderMap();
        for (Map.Entry<Integer, String> headerEntry : tableHeaderMap.entrySet()) {
            valueMap.put(headerEntry.getKey(), cellMap.get(headerEntry.getValue()));
        }

        List<XWPFTableCell> tableCells = row.getTableCells();
        for (int cellNum = 1; cellNum <= tableCells.size(); cellNum++) {
            XWPFTableCell cell = tableCells.get(cellNum - 1);
            setCellText(cell, valueMap.get(cellNum), table.getWidth() / tableHeaderMap.size());
        }
        return this;
    }

    /**
     * 设置单元格内容
     *
     * @param cell  cell
     * @param text  文本内容
     * @param width 列宽
     */
    private void setCellText(XWPFTableCell cell, String text, int width) {

        cell.setWidthType(TableWidthType.DXA);

        CTTc cttc = cell.getCTTc();
        CTTblWidth ctTblWidth = cell.getCTTc().addNewTcPr().addNewTcW();
        ctTblWidth.setW(BigInteger.valueOf(width));
        ctTblWidth.isSetW();
        //水平居中
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        CTTcPr ctPr = cttc.addNewTcPr();
        //防止列宽被挤
        ctPr.addNewNoWrap();

        //垂直居中
        ctPr.addNewVAlign().setVal(STVerticalJc.CENTER);
        cttc.getPList().get(0).addNewPPr().addNewJc().setVal(STJc.CENTER);

        CTP ctP = (cell.getCTTc().sizeOfPArray() == 0) ? cell.getCTTc().addNewP() : cell.getCTTc().getPArray(0);
        XWPFParagraph para = cell.getParagraph(ctP);
        cell.setParagraph(para);
        //默认1倍行距
        para.setSpacingBetween(spacingBetween != 0 ? spacingBetween : 1);
        //默认0.5行段前距离
        para.setSpacingBeforeLines(spacingBeforeLines != 0 ? spacingBeforeLines : 50);
        //默认0.5行段后距离
        para.setSpacingAfterLines(spacingAfterLines != 0 ? spacingAfterLines : 50);
        XWPFRun run = para.createRun();
        run.setFontFamily(StringUtils.isNotBlank(this.fontFamily) ? this.fontFamily : FontFamily.BLACK.getValue());
        run.setText(text);
    }

    /**
     * 设置表格宽度 默认 8310
     *
     * @param with 宽度
     * @return tableBuilder
     */
    public TableBuilder setTableWith(String with) {
        CTTbl ctTbl = table.getCTTbl();
        CTTblPr tblPr = ctTbl.getTblPr() == null ? ctTbl.addNewTblPr() : ctTbl.getTblPr();
        CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();
        tblWidth.setType(STTblWidth.DXA);
        tblWidth.setW(new BigInteger(with));
        return this;
    }

    /**
     * 添加多行数据
     *
     * @param cellMapList mapList（表头,值）
     * @return TableBuilder
     */
    public TableBuilder addRows(List<Map<String, String>> cellMapList) {
        if (CollectionUtils.isEmpty(cellMapList)) {
            return this;
        }

        for (Map<String, String> cellMap : cellMapList) {
            XWPFTableRow row = table.createRow();
//            row.setHeight(300);
            Map<Integer, String> valueMap = new HashMap<>();
            Map<Integer, String> tableHeaderMap = getTableHeaderMap();
            for (Map.Entry<Integer, String> headerEntry : tableHeaderMap.entrySet()) {
                valueMap.put(headerEntry.getKey(), cellMap.get(headerEntry.getValue()));
            }

            List<XWPFTableCell> tableCells = row.getTableCells();
            for (int cellNum = 1; cellNum <= tableCells.size(); cellNum++) {
                XWPFTableCell cell = tableCells.get(cellNum - 1);
                setCellText(cell, valueMap.get(cellNum), table.getWidth() / tableHeaderMap.size());
            }
        }
        return this;
    }

    /**
     * 设置字体，默认黑体
     *
     * @param fontFamily 字体
     * @return tableBuilder
     */
    public TableBuilder setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
        return this;
    }

    /**
     * 设置行距倍数 默认1倍
     *
     * @param spacing 行距倍数
     * @return tableBuilder
     */
    public TableBuilder setSpacingBetween(double spacing) {
        this.spacingBetween = spacing;
        return this;
    }

    /**
     * 设置段前距离 默认50（0.5行）
     *
     * @param spacingBeforeLines 段前距离
     * @return tableBuilder
     */
    public TableBuilder setSpacingBeforeLines(int spacingBeforeLines) {
        this.spacingBeforeLines = spacingBeforeLines;
        return this;
    }

    /**
     * 设置段后距离 默认50（0.5行）
     *
     * @param spacingAfterLines 段后距离
     * @return tableBuilder
     */
    public TableBuilder setSpacingAfterLines(int spacingAfterLines) {
        this.spacingAfterLines = spacingAfterLines;
        return this;
    }
}
