/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.util.excel;

import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class ExcelParser {
    private final Workbook workbook;

    public ExcelParser(InputStream inputStream) throws IOException {
        this.workbook = WorkbookFactory.create(inputStream);
    }


    public ExcelVo parseToObject() {
        ExcelVo excelVo = new ExcelVo();
        if (workbook != null) {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                Row headRow = sheet.getRow(sheet.getFirstRowNum());
                ExcelVo.SheetVo sheetVo = excelVo.addSheet(new ExcelVo.SheetVo(sheet.getSheetName()));
                for (Iterator<Cell> cellIterator = headRow.cellIterator(); cellIterator.hasNext(); ) {
                    Cell cell = cellIterator.next();
                    String cellValue = getCellValue(cell);
                    sheetVo.addHeader(new ExcelVo.SheetVo.HeaderVo(cellValue, cell.getColumnIndex()));
                }
                for (int j = sheet.getFirstRowNum() + 1; j <= sheet.getLastRowNum(); j++) {
                    Row row = sheet.getRow(j);
                    ExcelVo.SheetVo.RowVo rowVo = new ExcelVo.SheetVo.RowVo();
                    for (ExcelVo.SheetVo.HeaderVo head : sheetVo.getHeaderList()) {
                        Cell cell = row.getCell(head.getIndex());
                        ExcelVo.SheetVo.RowVo.ColumnVo columnVo = new ExcelVo.SheetVo.RowVo.ColumnVo(getCellValue(cell), head.getIndex());
                        rowVo.addColumn(columnVo);
                    }
                    sheetVo.addRow(rowVo);
                }
            }
        }
        return excelVo;
    }

    private static String getCellValue(Cell cell) {
        String cellValue;
        if (cell == null) {
            cellValue = null;
        } else {
            if (cell.getCellType() == CellType.NUMERIC) {
                double d = cell.getNumericCellValue();
                cellValue = (int) d + "";
            } else if (cell.getCellType() == CellType.STRING) {
                cellValue = cell.getStringCellValue();
            } else {
                cellValue = null;
            }
        }
        if (cellValue != null) {
            return cellValue.trim();
        } else {
            return "";
        }
    }
}
