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
