/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */
package codedriver.framework.util.word.table;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
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

    /**
     * 不含表头建表
     *
     * @param table table
     */
    public TableBuilder(XWPFTable table) {
        this.table = table;
    }

    /**
     * 含有表头建表
     *
     * @param table          table
     * @param tableHeaderMap 1->列1 , 2->列2 , 3->列3
     */
    public TableBuilder(XWPFTable table, Map<Integer, String> tableHeaderMap) {
        this.table = table;

        //表风格 、 宽度
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        tblPr.getTblW().setType(STTblWidth.DXA);
//        tblPr.getTblW().setW(new BigInteger("8310"));


        this.tableHeaderMap = tableHeaderMap;
        XWPFTableRow row = table.getRow(0);
        for (int cellNum = 1; cellNum <= tableHeaderMap.size(); cellNum++) {
            XWPFTableCell xwpfTableCell;
            if (cellNum == 1) {
                xwpfTableCell = row.getCell(0);
            } else {
                xwpfTableCell = row.addNewTableCell();
            }
            xwpfTableCell.setText(tableHeaderMap.get(cellNum));

            //单元格风格 、 平分表格宽度
            CTTcPr tcpr = xwpfTableCell.getCTTc().addNewTcPr();
            CTTblWidth cellw = tcpr.addNewTcW();
            cellw.setType(STTblWidth.DXA);
            cellw.setW(BigInteger.valueOf(2077));

            //垂直居中
            CTVerticalJc va = tcpr.addNewVAlign();
            va.setVal(STVerticalJc.CENTER);


            //水平居中
            List<XWPFParagraph> paragraphs = xwpfTableCell.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                paragraph.setAlignment(ParagraphAlignment.CENTER);
            }
        }
    }

    /**
     * 添加表头
     *
     * @param tableHeaderMap 1->列1 , 2->列2 , 3->列3
     * @return TableBuilder
     */
    public TableBuilder addTableHeaderMap(Map<Integer, String> tableHeaderMap) {
        if (MapUtils.isNotEmpty(tableHeaderMap)) {


//            //表风格 、 宽度
//            CTTblPr tblPr = table.getCTTbl().getTblPr();
//            tblPr.getTblW().setType(STTblWidth.DXA);
//            tblPr.getTblW().setW(new BigInteger("8310"));
//
//            //风格
            CTTblPr tblPr2 = table.getCTTbl().getTblPr();
            CTString styleStr = tblPr2.addNewTblStyle();
            styleStr.setVal("StyledTable");



            this.tableHeaderMap = tableHeaderMap;
            XWPFTableRow row = table.getRow(0);
            for (int cellNum = 1; cellNum <= tableHeaderMap.size(); cellNum++) {
                XWPFTableCell cell;
                if (cellNum == 1) {
                    cell = row.getCell(0);
                } else {
                    cell = row.addNewTableCell();
                }
                cell.setText(tableHeaderMap.get(cellNum));
//                xwpfTableCell.setWidth("2700");

                //单元格风格 、 平分表格宽度
                CTTcPr tcPr = cell.getCTTc().addNewTcPr();

                tcPr.addNewTcW().setW(BigInteger.valueOf(1600));
//                cellw.setType(STTblWidth.PCT);
//                cellw.setW(BigInteger.valueOf(20));




                //垂直居中
                CTVerticalJc va = tcPr.addNewVAlign();
                va.setVal(STVerticalJc.CENTER);

                //水平居中
                List<XWPFParagraph> paragraphs = cell.getParagraphs();
                for (XWPFParagraph paragraph : paragraphs) {
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                }
            }
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
        Map<Integer, String> valueMap = new HashMap<>();
        Map<Integer, String> tableHeaderMap = getTableHeaderMap();


        for (Map.Entry<Integer, String> headerEntry : tableHeaderMap.entrySet()) {
            valueMap.put(headerEntry.getKey(), cellMap.get(headerEntry.getValue()));
        }

        List<XWPFTableCell> tableCells = row.getTableCells();
        for (int cellNum = 1; cellNum <= tableCells.size(); cellNum++) {
            XWPFTableCell cell = tableCells.get(cellNum - 1);
            //内容
            cell.setText(valueMap.get(cellNum));

            //单元格风格 、 平分表格宽度
            CTTcPr tcpr = cell.getCTTc().addNewTcPr();
            CTTblWidth cellw = tcpr.addNewTcW();
            cellw.setType(STTblWidth.DXA);
            cellw.setW(BigInteger.valueOf(360 * 5));

            //垂直居中
            CTVerticalJc va = tcpr.addNewVAlign();
            va.setVal(STVerticalJc.BOTTOM);

            //水平居中
            List<XWPFParagraph> paragraphs = cell.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                paragraph.setAlignment(ParagraphAlignment.CENTER);
            }

        }
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
            Map<Integer, String> valueMap = new HashMap<>();
            Map<Integer, String> tableHeaderMap = getTableHeaderMap();
            for (Map.Entry<Integer, String> headerEntry : tableHeaderMap.entrySet()) {
                valueMap.put(headerEntry.getKey(), cellMap.get(headerEntry.getValue()));
            }

            List<XWPFTableCell> tableCells = row.getTableCells();
            for (int cellNum = 1; cellNum <= tableCells.size(); cellNum++) {
                XWPFTableCell cell = tableCells.get(cellNum - 1);
                //内容
                cell.setText(valueMap.get(cellNum));

                //单元格风格 、 平分表格宽度
                CTTcPr tcpr = cell.getCTTc().addNewTcPr();
                CTTblWidth cellw = tcpr.addNewTcW();
                cellw.setType(STTblWidth.DXA);
                cellw.setW(BigInteger.valueOf(360 * 5));

                //垂直居中
                CTVerticalJc va = tcpr.addNewVAlign();
                va.setVal(STVerticalJc.BOTTOM);

                //水平居中
                List<XWPFParagraph> paragraphs = cell.getParagraphs();
                for (XWPFParagraph paragraph : paragraphs) {
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                }

            }
        }
        return this;
    }
}
