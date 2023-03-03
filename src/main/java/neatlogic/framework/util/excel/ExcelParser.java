/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
