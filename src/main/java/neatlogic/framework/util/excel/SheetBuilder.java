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