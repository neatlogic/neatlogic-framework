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
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SheetBuilder {
    private final String id;
    private final String sheetName;
    private List<String> headerList;
    private List<Map<String, DataCell>> dataList;
    private List<String> columnList;
    private Sheet sheet;
    private Workbook workbook;
    private ExcelBuilder excelBuilder;
    private final Map<String, String[]> validationMap = new HashMap<>();
    private CellStyle cellStyle;//设置默认cellStyle，防止在每个cell里面创建导致，创建过多异常

    public static class DataCell {
        private Object value;
        private int colspan = 0;
        private int rowspan = 0;
        private String backgroundColor;
        private String fontColor;

        public DataCell(Object value) {
            this.value = value;
        }

        public DataCell(Object value, int rowspan, int colspan) {
            this.value = value;
            this.rowspan = rowspan;
            this.colspan = colspan;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public int getRowspan() {
            if (rowspan < 0) {
                rowspan = 0;
            }
            return rowspan;
        }

        public void setRowspan(int rowspan) {
            this.rowspan = rowspan;
        }

        public int getColspan() {
            if (colspan < 0) {
                colspan = 0;
            }
            return colspan;
        }

        public String getBackgroundColor() {
            return backgroundColor;
        }

        public void setBackgroundColor(String backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        public String getFontColor() {
            return fontColor;
        }

        public void setFontColor(String fontColor) {
            this.fontColor = fontColor;
        }

        public void setColspan(int colspan) {
            this.colspan = colspan;
        }
    }

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

    public SheetBuilder withDataList(List<Map<String, DataCell>> dataList) {
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

    public List<Map<String, DataCell>> getDataList() {
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
        Map<String, DataCell> dataCellMap = dataMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> new DataCell(entry.getValue())));
        this.addDataCell(dataCellMap);
    }

    // 将#xxxxxx颜色编码转换为RGB byte数组
    private static byte[] hexToRGB(String colorStr) {
        // 去掉前缀#号
        colorStr = colorStr.startsWith("#") ? colorStr.substring(1) : colorStr;

        // 将16进制颜色编码解析为int类型的RGB值
        int red = Integer.parseInt(colorStr.substring(0, 2), 16);
        int green = Integer.parseInt(colorStr.substring(2, 4), 16);
        int blue = Integer.parseInt(colorStr.substring(4, 6), 16);

        // 返回byte数组
        return new byte[]{(byte) red, (byte) green, (byte) blue};
    }

    private void setCellBackgroundColor(Cell cell, String color) {
        // 设置背景色
        byte[] rgb = hexToRGB(color);
        if (workbook instanceof XSSFWorkbook) {
            XSSFCellStyle style = ((XSSFWorkbook) workbook).createCellStyle();
            XSSFColor xssColor = new XSSFColor(rgb, null);
            style.setFillForegroundColor(xssColor);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cell.setCellStyle(style);
        } else if (workbook instanceof HSSFWorkbook) {
            HSSFPalette palette = ((HSSFWorkbook) workbook).getCustomPalette();
            CellStyle style = workbook.createCellStyle();
            short red = (short) (rgb[0] & 0xFF);
            short green = (short) (rgb[1] & 0xFF);
            short blue = (short) (rgb[2] & 0xFF);
            HSSFColor hssColor = palette.findSimilarColor(red, green, blue); // RGB 对应的颜色
            short colorIndex = hssColor.getIndex();
            style.setFillForegroundColor(colorIndex);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cell.setCellStyle(style);
        }
    }

    public void addDataCell(Map<String, DataCell> dataMap) {
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
                    DataCell dataCell = dataMap.get(column);
                    if (dataCell.getValue() != null) {
                        cellValue = dataCell.getValue().toString();
                        if (cellValue.length() > SpreadsheetVersion.EXCEL2007.getMaxTextLength()) {
                            cellValue = cellValue.substring(0, SpreadsheetVersion.EXCEL2007.getMaxTextLength() - 3) + "...";
                        }
                    }
                    //设置跨行跨列
                    if (dataCell.getColspan() > 0 || dataCell.getRowspan() > 0) {
                        this.sheet.addMergedRegion(new CellRangeAddress(cell.getRowIndex(), cell.getRowIndex() + dataCell.getRowspan(), cell.getColumnIndex(), cell.getColumnIndex() + dataCell.getColspan()));
                    }
                    if (StringUtils.isNotBlank(dataCell.getBackgroundColor())) {
                        this.setCellBackgroundColor(cell, dataCell.getBackgroundColor());
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
            for (Map<String, Object> dataMap : dataMapList) {
                Map<String, DataCell> dataCellMap = dataMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> new DataCell(entry.getValue())));
                this.addDataCell(dataCellMap);
            }
            //dataMapList.forEach(this::addData);
        }
    }

    public void addDataCellList(List<Map<String, DataCell>> dataMapList) {
        if (CollectionUtils.isNotEmpty(dataMapList)) {
            dataMapList.forEach(this::addDataCell);
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