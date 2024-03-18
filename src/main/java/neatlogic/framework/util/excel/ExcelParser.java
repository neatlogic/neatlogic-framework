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
