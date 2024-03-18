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


import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SheetBuilder {
    private final String id;
    private final String sheetName;
    private List<String> headerList;
    private List<Map<String, Object>> dataList;
    private List<String> columnList;
    private Sheet sheet;
    private Workbook workbook;
    private ExcelBuilder excelBuilder;
    private final Map<String, String[]> validationMap = new HashMap<>();
    private CellStyle cellStyle;//设置默认cellStyle，防止在每个cell里面创建导致，创建过多异常

    SheetBuilder(String id, String sheetName) {
        this.id = id;
        this.sheetName = sheetName;
    }

    SheetBuilder(String sheetName) {
        this.id = sheetName;
        this.sheetName = sheetName;
    }

    public SheetBuilder withHeaderList(List<String> headerList) {
        this.headerList = headerList;
        return this;
    }

    public SheetBuilder withDataList(List<Map<String, Object>> dataList) {
        this.dataList = dataList;
        return this;
    }

    public SheetBuilder withColumnList(List<String> columnList) {
        this.columnList = columnList;
        return this;
    }

    public SheetBuilder withCellStyle(CellStyle cellStyle) {
        this.cellStyle = cellStyle;
        return this;
    }

    public Map<String, String[]> getValidationMap() {
        return validationMap;
    }


    public List<String> getHeaderList() {
        return headerList;
    }

    public String getSheetName() {
        return this.sheetName;
    }

    public String getId() {
        return this.id;
    }

    public List<Map<String, Object>> getDataList() {
        return this.dataList;
    }

    public List<String> getColumnList() {
        return this.columnList;
    }

    public Sheet getSheet() {
        return sheet;
    }

    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    public void setWorkbook(Workbook workbook) {
        this.workbook = workbook;
    }


    public void setExcelBuilder(ExcelBuilder excelBuilder) {
        this.excelBuilder = excelBuilder;
    }


    public void addData(Map<String, Object> dataMap) {
        if (this.sheet != null && workbook != null && excelBuilder != null) {
            int lastRowNum = this.sheet.getLastRowNum();
            lastRowNum++;
            Row row = sheet.createRow(lastRowNum);
            int j = 0;
            for (String column : columnList) {
                Cell cell = row.createCell(j);
                makeupBody(cell);
                String cellValue = null;
                if (dataMap.get(column) != null) {
                    cellValue = dataMap.get(column).toString();
                    if (cellValue.length() > SpreadsheetVersion.EXCEL2007.getMaxTextLength()) {
                        cellValue = cellValue.substring(0, SpreadsheetVersion.EXCEL2007.getMaxTextLength() - 3) + "...";
                    }
                }
                cell.setCellValue(cellValue);
                j++;
            }
        } else {
            if (this.dataList == null) {
                dataList = new ArrayList<>();
            }
            this.dataList.add(dataMap);
        }
    }

    public void addDataList(List<Map<String, Object>> dataMapList) {
        if (CollectionUtils.isNotEmpty(dataMapList)) {
            dataMapList.forEach(this::addData);
        }
    }

    public void addValidation(String key, String[] validateList) {
        this.validationMap.put(key, validateList);
    }


    private void makeupBody(Cell cell) {
        if (workbook != null && excelBuilder != null) {
            cell.setCellStyle(cellStyle);
        }
    }
}