/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;

import java.util.List;
import java.util.Map;

public class ExcelBuilder {
    private Class<? extends Workbook> workbookClass;
    private Workbook workbook;
    private Sheet sheet;
    private String sheetName;
    private List<String> headerList;
    private List<String> columnList;
    private List<Map<String, Object>> dataList;
    private HSSFColor.HSSFColorPredefined headBgColor;
    private HSSFColor.HSSFColorPredefined headFontColor;

    public ExcelBuilder(Class<? extends Workbook> workbookClass) {
        this.workbookClass = workbookClass;
    }

    public ExcelBuilder withWorkbook(Workbook workbook) {
        this.workbook = workbook;
        return this;
    }

    public ExcelBuilder withSheet(Sheet sheet) {
        this.sheet = sheet;
        return this;
    }

    public ExcelBuilder withSheetName(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }

    public ExcelBuilder withHeaderList(List<String> headerList) {
        this.headerList = headerList;
        return this;
    }

    public ExcelBuilder withColumnList(List<String> columnList) {
        this.columnList = columnList;
        return this;
    }

    public ExcelBuilder withDataList(List<Map<String, Object>> dataList) {
        this.dataList = dataList;
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

    private void makeupBody(Cell cell) {
        if (workbook != null) {
            CellStyle style = workbook.createCellStyle();
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            cell.setCellStyle(style);
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
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            cell.setCellStyle(style);
        }
    }

    public Workbook build() {
        if (this.workbook == null) {
            try {
                this.workbook = (Workbook) (Class.forName(workbookClass.getName()).newInstance());
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new RuntimeException("无法使用" + workbookClass.getSimpleName() + "创建工作簿：" + e.getMessage(), e);
            }
        }
        if (!this.workbook.getClass().getSimpleName().equals(workbookClass.getSimpleName())) {
            throw new RuntimeException("工作簿类型不正确，定义是:" + workbookClass.getSimpleName() + "，实际是：" + workbook.getClass().getSimpleName());
        }
        if (this.sheet == null) {
            if (StringUtils.isNotBlank(this.sheetName)) {
                this.sheet = this.workbook.createSheet(this.sheetName);
            } else {
                this.sheet = this.workbook.createSheet();
            }
        }
        //生成标题行
        if (CollectionUtils.isNotEmpty(headerList)) {
            Row headerRow = sheet.createRow(0);
            int i = 0;
            for (String header : headerList) {
                Cell cell = headerRow.createCell(i);
                makeupHeader(cell);
                cell.setCellValue(header);
                i++;
            }
        }
        //生成数据行
        if (CollectionUtils.isNotEmpty(columnList) && CollectionUtils.isNotEmpty(dataList)) {
            int lastRowNum = sheet.getLastRowNum();
            for (Map<String, Object> dataMap : dataList) {
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
        }
        return this.workbook;
    }

    public static void main(String[] a) {
        ExcelBuilder builder = new ExcelBuilder(HSSFWorkbook.class);
        builder.build();
    }
}
