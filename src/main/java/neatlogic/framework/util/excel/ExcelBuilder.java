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

import neatlogic.framework.exception.excel.UnableToCreateWorkbookException;
import neatlogic.framework.exception.excel.WorkbookTypeIsIncorrectException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.util.*;

public class ExcelBuilder {
    private static final String SHEET_ILLEGAL_SYMBOL_REGEX = "(\\*|/|:|\\\\|\\[|\\]|\\?|：|？)";
    private final Class<? extends Workbook> workbookClass;
    private Workbook workbook;
    //private Sheet sheet;
    // private String sheetName;
    //private List<String> headerList;
    //private List<String> columnList;
    //private List<Map<String, Object>> dataList;
    private HSSFColor.HSSFColorPredefined headBgColor;
    private HSSFColor.HSSFColorPredefined headFontColor;
    private HSSFColor.HSSFColorPredefined borderColor;
    private Integer columnWidth;
    private final List<SheetBuilder> sheetBuilderList = new ArrayList<>();

    private final Map<String, SheetBuilder> sheetBuilderMap = new HashMap<>();
    private final CellStyle cellStyle;//设置默认cellStyle，防止在每个cell里面创建导致，创建过多异常

    private String filePath;

    private String name;

    public ExcelBuilder(Class<? extends Workbook> workbookClass) {
        this.workbookClass = workbookClass;
        //初始化默认cellStyle
        Workbook workbook = build();
        CellStyle style = workbook.createCellStyle();
        if (this.borderColor != null) {
            style.setBottomBorderColor(borderColor.getIndex());
            style.setTopBorderColor(borderColor.getIndex());
            style.setLeftBorderColor(borderColor.getIndex());
            style.setRightBorderColor(borderColor.getIndex());
        }
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        cellStyle = style;
    }

    public ExcelBuilder withWorkbook(Workbook workbook) {
        this.workbook = workbook;
        return this;
    }

    public List<SheetBuilder> getSheetBuilderList() {
        return sheetBuilderList;
    }

    public SheetBuilder getSheetBuilderById(String sheetId) {
        if (CollectionUtils.isNotEmpty(sheetBuilderList)) {
            Optional<SheetBuilder> op = sheetBuilderList.stream().filter(d -> d.getId().equals(sheetId)).findFirst();
            if (op.isPresent()) {
                return op.get();
            }
        }
        return null;
    }

    public SheetBuilder getSheetBuilderByName(String sheetName) {
        if (CollectionUtils.isNotEmpty(sheetBuilderList)) {
            Optional<SheetBuilder> op = sheetBuilderList.stream().filter(d -> d.getId().equals(sheetName)).findFirst();
            if (op.isPresent()) {
                return op.get();
            }
        }
        return null;
    }


    public SheetBuilder addSheet(String sheetId, String sheetName) {
        SheetBuilder sheetBuilder = new SheetBuilder(sheetId, sheetName);
        sheetBuilder.withCellStyle(cellStyle);
        sheetBuilderList.add(sheetBuilder);
        return sheetBuilder;
    }

    public SheetBuilder addSheet(SheetBuilder sheetBuilder) {
        sheetBuilderList.add(sheetBuilder);
        return sheetBuilder;
    }

    public SheetBuilder addSheet(String sheetName) {
        SheetBuilder sheetBuilder = new SheetBuilder(sheetName, sheetName);
        sheetBuilder.withCellStyle(cellStyle);
        sheetBuilderList.add(sheetBuilder);
        return sheetBuilder;
    }

    /*
    字符数
     */
    public ExcelBuilder withColumnWidth(Integer columnWidth) {
        this.columnWidth = columnWidth;
        return this;
    }

    public ExcelBuilder withFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public ExcelBuilder withName(String name) {
        this.name = name;
        return this;
    }


    public ExcelBuilder withHeadBgColor(HSSFColor.HSSFColorPredefined color) {
        this.headBgColor = color;
        return this;
    }

    public ExcelBuilder withHeadFontColor(HSSFColor.HSSFColorPredefined color) {
        this.headFontColor = color;
        return this;
    }

    public ExcelBuilder withBorderColor(HSSFColor.HSSFColorPredefined color) {
        this.borderColor = color;
        return this;
    }


    public HSSFColor.HSSFColorPredefined getBorderColor() {
        return this.borderColor;
    }

    private void makeupBody(Cell cell) {
        if (workbook != null) {
            cell.setCellStyle(cellStyle);
        }
    }

    private void makeupHeader(Cell cell) {
        if (workbook != null) {
            CellStyle style = workbook.createCellStyle();
            if (this.headBgColor != null) {
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                style.setFillBackgroundColor(headBgColor.getIndex());// 设置背景色
                style.setFillForegroundColor(headBgColor.getIndex());
            }
            if (this.headFontColor != null) {
                Font font = workbook.createFont();
                font.setColor(headFontColor.getIndex());
                style.setFont(font);
            }
            if (this.borderColor != null) {
                style.setBottomBorderColor(borderColor.getIndex());
                style.setTopBorderColor(borderColor.getIndex());
                style.setLeftBorderColor(borderColor.getIndex());
                style.setRightBorderColor(borderColor.getIndex());
            }
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            cell.setCellStyle(style);
        }
    }

    public String getFilePath() {
        return this.filePath;
    }

    public String getName() {
        return this.name;
    }

    public Workbook build() {
        if (this.workbook == null) {
            try {
                this.workbook = (Workbook) (Class.forName(workbookClass.getName()).newInstance());
            } catch (Exception e) {
                throw new UnableToCreateWorkbookException(workbookClass.getSimpleName(), e.getMessage());
            }
        }
        if (!this.workbook.getClass().getSimpleName().equals(workbookClass.getSimpleName())) {
            throw new WorkbookTypeIsIncorrectException(workbookClass.getSimpleName(), workbook.getClass().getSimpleName());
        }


        if (CollectionUtils.isNotEmpty(this.sheetBuilderList)) {
            for (SheetBuilder sheetBuilder : this.sheetBuilderList) {
                Sheet sheet = null;
                String sheetName = sheetBuilder.getSheetName();
                if (sheetName != null) {
                    // sheet名称去掉不合法字符
                    sheetName = sheetName.replaceAll(SHEET_ILLEGAL_SYMBOL_REGEX, "");
                }
                if (sheetName != null && sheetName.length() > 31) {
                    // sheet名称不能超过31个字符
                    sheetName = sheetName.substring(0, 31);
                }
                if (StringUtils.isNotBlank(sheetName)) {
                    // 判断是否存在同名sheet
                    if (this.workbook.getSheet(sheetName) != null) {
                        sheetName = sheetName.substring(0, Math.min(sheetName.length(), 28));
                        for (int i = 2; i < 10; i++) {
                            sheetName = sheetName + "(" + i + ")";
                            if (this.workbook.getSheet(sheetName) == null) {
                                break;
                            }
                        }
                    }
                    sheet = this.workbook.createSheet(sheetName);
                } else {
                    sheet = this.workbook.createSheet();
                }
                sheetBuilder.setExcelBuilder(this);
                sheetBuilder.setSheet(sheet);
                sheetBuilder.setWorkbook(this.workbook);

                //设置列宽
                if (this.columnWidth != null && CollectionUtils.isNotEmpty(sheetBuilder.getColumnList())) {
                    for (int i = 0; i < sheetBuilder.getColumnList().size(); i++) {
                        sheet.setColumnWidth(i, this.columnWidth * 256);
                    }
                } else {
                    sheet.setDefaultColumnWidth(15);
                }
                //生成标题行
                if (CollectionUtils.isNotEmpty(sheetBuilder.getHeaderList())) {
                    Row headerRow = sheet.createRow(0);
                    int i = 0;
                    for (String header : sheetBuilder.getHeaderList()) {
                        Cell cell = headerRow.createCell(i);
                        makeupHeader(cell);
                        cell.setCellValue(header);
                        i++;
                    }
                }
                //生成校验规则
                if (MapUtils.isNotEmpty(sheetBuilder.getValidationMap()) && CollectionUtils.isNotEmpty(sheetBuilder.getColumnList())) {
                    for (String key : sheetBuilder.getValidationMap().keySet()) {
                        String[] validateList = sheetBuilder.getValidationMap().get(key);
                        int index = -1;
                        for (int i = 0; i < sheetBuilder.getColumnList().size(); i++) {
                            if (key.equals(sheetBuilder.getColumnList().get(i))) {
                                index = i;
                                break;
                            }
                        }
                        if (index > -1) {
                            DataValidationHelper dvHelper = sheet.getDataValidationHelper();
                            DataValidationConstraint dvConstraint = dvHelper.createExplicitListConstraint(validateList);
                            CellRangeAddressList addressList = new CellRangeAddressList(1, 65535, index, index);
                            DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
                            validation.setSuppressDropDownArrow(true);
                            validation.setShowErrorBox(true);
                            sheet.addValidationData(validation);
                        }
                    }
                }
                //生成数据行
                if (CollectionUtils.isNotEmpty(sheetBuilder.getColumnList()) && CollectionUtils.isNotEmpty(sheetBuilder.getColumnList()) && CollectionUtils.isNotEmpty(sheetBuilder.getDataList())) {
                    int lastRowNum = sheet.getLastRowNum();
                    for (Map<String, Object> dataMap : sheetBuilder.getDataList()) {
                        lastRowNum++;
                        Row row = sheet.createRow(lastRowNum);
                        int j = 0;
                        for (String column : sheetBuilder.getColumnList()) {
                            Cell cell = row.createCell(j);
                            makeupBody(cell);
                            cell.setCellValue(dataMap.get(column) == null ? null : dataMap.get(column).toString());
                            j++;
                        }
                    }
                }
            }
        }

        return this.workbook;
    }

    /*
    public void addRow(Map<String, Object> dataMap) {
        if (this.workbook == null) {
            this.build();
        }
        if (this.workbook != null && CollectionUtils.isNotEmpty(columnList)) {
            int lastRowNum = sheet.getLastRowNum();
            lastRowNum++;
            Row row = sheet.createRow(lastRowNum);
            int j = 0;
            for (String column : columnList) {
                Cell cell = row.createCell(j);
                makeupBody(cell);
                cell.setCellValue(dataMap.get(column) == null ? null : dataMap.get(column).toString());
                j++;
            }
        }
    }*/

    /*public static void main(String[] a) {
        ExcelBuilder builder = new ExcelBuilder(HSSFWorkbook.class);
        builder.build();
    }*/
}
