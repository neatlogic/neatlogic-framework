/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.util.excel;


import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SheetBuilder {
    private final String sheetName;
    private List<String> headerList;
    private List<Map<String, Object>> dataList;
    private List<String> columnList;
    private Sheet sheet;
    private Workbook workbook;
    private ExcelBuilder excelBuilder;
    private final Map<String, String[]> validationMap = new HashMap<>();

    SheetBuilder(String sheetName) {
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

    public Map<String, String[]> getValidationMap() {
        return validationMap;
    }


    public List<String> getHeaderList() {
        return headerList;
    }

    public String getSheetName() {
        return this.sheetName;
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
                cell.setCellValue(dataMap.get(column) == null ? null : dataMap.get(column).toString());
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
            CellStyle style = workbook.createCellStyle();
            if (this.excelBuilder.getBorderColor() != null) {
                style.setBottomBorderColor(this.excelBuilder.getBorderColor().getIndex());
                style.setTopBorderColor(this.excelBuilder.getBorderColor().getIndex());
                style.setLeftBorderColor(this.excelBuilder.getBorderColor().getIndex());
                style.setRightBorderColor(this.excelBuilder.getBorderColor().getIndex());
            }
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            cell.setCellStyle(style);
        }
    }
}