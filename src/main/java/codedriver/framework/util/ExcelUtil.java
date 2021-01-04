package codedriver.framework.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import codedriver.framework.exception.file.EmptyExcelException;
import codedriver.framework.exception.file.ExcelLostChannelUuidException;

/**
 * @program: codedriver
 * @description:
 * @create: 2020-03-31 17:00
 **/
public class ExcelUtil {

    static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    /**
    * @Description: excel 导出
    * @Param: [headerList, columnList, columnSelectValueList, dataMapList, os]
    * @return: void
    */
    public static void exportExcel(List<String> headerList, List<String> columnList, List<List<String>> columnSelectValueList, List<Map<String, String>> dataMapList, OutputStream os)
    {
        @SuppressWarnings("resource")
        HSSFWorkbook workbook = new HSSFWorkbook();
        workbook.createSheet("sheet01");

        // 声明一个工作薄   生成一个表格
        HSSFSheet sheet = workbook.getSheet("sheet01");
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth((short) 15);
        // 生成一个样式
        HSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(HSSFColorPredefined.SKY_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        // 生成一个字体
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColorPredefined.VIOLET.getIndex());
        font.setFontHeightInPoints((short) 12);
         font.setBold(true);
        // 把字体应用到当前的样式
        style.setFont(font);
        // 生成并设置另一个样式
        HSSFCellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(HSSFColorPredefined.LIGHT_YELLOW.getIndex());
        style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style2.setBorderBottom(BorderStyle.THIN);
        style2.setBorderLeft(BorderStyle.THIN);
        style2.setBorderRight(BorderStyle.THIN);
        style2.setBorderTop(BorderStyle.THIN);
        style2.setAlignment(HorizontalAlignment.CENTER);
        style2.setVerticalAlignment(VerticalAlignment.CENTER);
        // 生成另一个字体
        HSSFFont font2 = workbook.createFont();
        font2.setBold(true);
        // 把字体应用到当前的样式
        style2.setFont(font2);

        HSSFFont font3 = workbook.createFont();
        font3.setColor(HSSFColorPredefined.BLUE.getIndex());

//        // 声明一个画图的顶级管理器
//        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
//        // 定义注释的大小和位置,详见文档
//        HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));
//        // 设置注释内容
//        comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));
//        // 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
//        comment.setAuthor("leno");

        // 产生表格标题行
        HSSFRow row = sheet.createRow(0);
        int i = 0;
        if(CollectionUtils.isNotEmpty(headerList)) {
        	for (String header : headerList) {
                HSSFCell cell = row.createCell(i);
                cell.setAsActiveCell();
                cell.setCellStyle(style);
                HSSFRichTextString text = new HSSFRichTextString(header);
                cell.setCellValue(text);
                if (CollectionUtils.isNotEmpty(columnSelectValueList)){
                    List<String> defaultValueList = columnSelectValueList.get(i);
                    //行添加下拉框
                    if (CollectionUtils.isNotEmpty(defaultValueList)){
                        String[] values = new String[defaultValueList.size()];
                        defaultValueList.toArray(values);
                        CellRangeAddressList region = new CellRangeAddressList();
                        region.addCellRangeAddress(1, i, SpreadsheetVersion.EXCEL97.getLastRowIndex(), i);
                        DVConstraint constraint = DVConstraint.createExplicitListConstraint(values);
                        HSSFDataValidation data_validation_list = new HSSFDataValidation(region, constraint);
                        //将有效性验证添加到表单
                        sheet.addValidationData(data_validation_list);
                    }
                }
                i++;
            }
        }


        if (CollectionUtils.isNotEmpty(dataMapList) && CollectionUtils.isNotEmpty(columnList)){
            int index = 0;
            for (Map<String, String> dataMap : dataMapList){
                index++;
                row = sheet.createRow(index);
                int j = 0;
                for (String column :columnList){
                    HSSFCell cell = row.createCell(j);
                    cell.setCellStyle(style2);
                    HSSFRichTextString richString = new HSSFRichTextString(dataMap.get(column));
                    richString.applyFont(font3);
                    cell.setCellValue(richString);
                    j++;
                }
            }
        }
        try {
            workbook.write(os);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (os != null) {
                try {
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
    * @Description: 表头导出
    * @Param: [headerList, columnSelectValueList, os]
    * @return: void
    */
    public static void exportExcelHeaders(List<String> headerList, List<List<String>> columnSelectValueList, OutputStream os){
        exportExcel(headerList, null, columnSelectValueList, null, os);
    }

    /**
    * @Description: 不包含下拉框默认值导出
    * @Param: [headerList, columnList, dataMapList, os]
    * @return: void
    */
    public static void exportExcel(List<String> headerList, List<String> columnList, List<Map<String, String>> dataMapList, OutputStream os){
        exportExcel(headerList, columnList, null, dataMapList, os);
    }

    public static HSSFWorkbook createExcel(HSSFWorkbook workbook, List<String> headerList, List<String> columnList, List<List<String>> columnSelectValueList, List<Map<String, String>> dataMapList) {
    	HSSFSheet sheet = null;
    	if(workbook == null) {
    		workbook = new HSSFWorkbook();
            workbook.createSheet("sheet01");

            // 声明一个工作薄   生成一个表格
            sheet = workbook.getSheet("sheet01");
            // 设置表格默认列宽度为15个字节
            sheet.setDefaultColumnWidth((short) 15);
            // 生成一个样式
            HSSFCellStyle style = workbook.createCellStyle();
            // 设置这些样式
            style.setFillForegroundColor(HSSFColorPredefined.SKY_BLUE.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setAlignment(HorizontalAlignment.CENTER);
            // 生成一个字体
            HSSFFont font = workbook.createFont();
            font.setColor(HSSFColorPredefined.VIOLET.getIndex());
            font.setFontHeightInPoints((short) 12);
            font.setBold(true);
            // 把字体应用到当前的样式
            style.setFont(font);


            // 产生表格标题行
            HSSFRow row = sheet.createRow(0);
            if(CollectionUtils.isNotEmpty(headerList)) {
                int i = 0;
            	for (String header : headerList) {
                    HSSFCell cell = row.createCell(i);
                    cell.setAsActiveCell();
                    cell.setCellStyle(style);
                    HSSFRichTextString text = new HSSFRichTextString(header);
                    cell.setCellValue(text);
                    if (CollectionUtils.isNotEmpty(columnSelectValueList)){
                        List<String> defaultValueList = columnSelectValueList.get(i);
                        //行添加下拉框
                        if (CollectionUtils.isNotEmpty(defaultValueList)){
                            String[] values = new String[defaultValueList.size()];
                            defaultValueList.toArray(values);
                            CellRangeAddressList region = new CellRangeAddressList();
                            region.addCellRangeAddress(1, i, SpreadsheetVersion.EXCEL97.getLastRowIndex(), i);
                            DVConstraint constraint = DVConstraint.createExplicitListConstraint(values);
                            HSSFDataValidation data_validation_list = new HSSFDataValidation(region, constraint);
                            //将有效性验证添加到表单
                            sheet.addValidationData(data_validation_list);
                        }
                    }
                    i++;
                }
            }
    	}else {
    		//sheet = workbook.getSheetAt(0);
    		sheet = workbook.getSheet("sheet01");
    	}

        if (CollectionUtils.isNotEmpty(dataMapList) && CollectionUtils.isNotEmpty(columnList)){
        	// 生成并设置另一个样式
        	HSSFCellStyle style2 = workbook.createCellStyle();
            style2.setFillForegroundColor(HSSFColorPredefined.LIGHT_YELLOW.getIndex());
            style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style2.setBorderBottom(BorderStyle.THIN);
            style2.setBorderLeft(BorderStyle.THIN);
            style2.setBorderRight(BorderStyle.THIN);
            style2.setBorderTop(BorderStyle.THIN);
            style2.setAlignment(HorizontalAlignment.CENTER);
            style2.setVerticalAlignment(VerticalAlignment.CENTER);
            // 生成另一个字体
            HSSFFont font2 = workbook.createFont();
            font2.setBold(true);
            // 把字体应用到当前的样式
            style2.setFont(font2);
        	HSSFFont font3 = workbook.createFont();
            font3.setColor(HSSFColorPredefined.BLUE.getIndex());

        	int lastRowNum = sheet.getLastRowNum();
            for (Map<String, String> dataMap : dataMapList){
            	lastRowNum++;
            	HSSFRow row = sheet.createRow(lastRowNum);
                int j = 0;
                for (String column :columnList){
                    HSSFCell cell = row.createCell(j);
                    cell.setCellStyle(style2);
                    HSSFRichTextString richString = new HSSFRichTextString(dataMap.get(column));
                    richString.applyFont(font3);
                    cell.setCellValue(richString);
                    j++;
                }
            }
        }

    	return workbook;
    }

    /**
     * 此方法可传入poi-3.8的SXSSFWorkbook来创建excel
     * 3.8以前的版本，单元格字符数有限
     * @param workbook
     * @param headerList
     * @param columnList
     * @param dataMapList
     * @return
     * @throws Exception
     */
    public static Workbook exportData(Workbook workbook, List<String> headerList, List<String> columnList, List<Map<String,Object>> dataMapList,Integer columnWidth,int num) throws Exception {
        // 生成一个表格
        Sheet sheet = workbook.createSheet();
        // 设置sheet名字
        workbook.setSheetName(num,"sheet-" + num);
        Map<String, CellStyle> cellStyle = getRowCellStyle(workbook);
        CellStyle firstRowcellStyle = cellStyle.get("firstRowcellStyle");
        CellStyle rowcellStyle = cellStyle.get("rowcellStyle");

        //生成标题行与正文行
        createRows(headerList, columnList, dataMapList, columnWidth, sheet, firstRowcellStyle, rowcellStyle, 0);
        return workbook;
    }


    /**
     * 导出工单上报模版方法
     * @param workbook
     * @param headerList
     * @param columnList
     * @param dataMapList
     * @param channelData
     * @param columnWidth
     * @return
     * @throws Exception
     */
    public static Workbook exportProcessTaskTemplate(Workbook workbook, List<String> headerList, List<String> columnList, List<Map<String,Object>> dataMapList,List<String> channelData,Integer columnWidth) throws Exception {
        // 生成一个表格
        Sheet sheet = workbook.createSheet();
        // 设置sheet名字
        workbook.setSheetName(0,"sheet");
        Map<String, CellStyle> cellStyle = getRowCellStyle(workbook);
        CellStyle firstRowcellStyle = cellStyle.get("firstRowcellStyle");
        CellStyle rowcellStyle = cellStyle.get("rowcellStyle");

        /** 生成服务信息行 */
        Row channelRow = sheet.createRow(0);
        for(int i = 0;i < channelData.size();i++){
            Cell cell = channelRow.createCell(i);
            cell.setCellValue(channelData.get(i));
        }

        createRows(headerList, columnList, dataMapList, columnWidth, sheet, firstRowcellStyle, rowcellStyle, 1);
        return workbook;
    }

    /**
     * 生成标题行与正文行
     * @param headerList
     * @param columnList
     * @param dataMapList
     * @param columnWidth
     * @param sheet
     * @param firstRowcellStyle
     * @param rowcellStyle
     * @param headRowNum 标题行开始行数
     */
    public static void createRows(List<String> headerList, List<String> columnList, List<Map<String, Object>> dataMapList, Integer columnWidth, Sheet sheet, CellStyle firstRowcellStyle, CellStyle rowcellStyle, int headRowNum) {
        //生成标题行
        Row headerRow = sheet.createRow(headRowNum);
        if (CollectionUtils.isNotEmpty(headerList)) {
            int i = 0;
            for (String header : headerList) {
                //设置列宽
                if (columnWidth != null && columnWidth > 0) {
                    sheet.setColumnWidth(i, columnWidth.intValue() * 256);
                }
                Cell cell = headerRow.createCell(i);
                cell.setCellStyle(firstRowcellStyle);
                cell.setCellValue(header);
                i++;
            }
        }
        //生成数据行
        if (CollectionUtils.isNotEmpty(columnList) && CollectionUtils.isNotEmpty(dataMapList)) {
            int lastRowNum = sheet.getLastRowNum();
            for (Map<String, Object> dataMap : dataMapList) {
                lastRowNum++;
                Row row = sheet.createRow(lastRowNum);
                int j = 0;
                for (String column : columnList) {
                    Cell cell = row.createCell(j);
                    cell.setCellStyle(rowcellStyle);
                    cell.setCellValue(dataMap.get(column) == null ? null : dataMap.get(column).toString());
                    j++;
                }
            }
        }
    }

    public static String getCellContent(Cell cell) {
        String cellContent = "";
        switch (cell.getCellType()) {
            case NUMERIC:
                cellContent = (int) cell.getNumericCellValue() + "";
                break;
            case STRING:
                cellContent = cell.getStringCellValue() + "";
                break;
            case BOOLEAN:
                cellContent = cell.getBooleanCellValue() + "";
                break;
            case BLANK:
                cellContent = "blank";
                break;
            case FORMULA:
                cellContent = cell.getCellFormula() + "";
                break;
            case ERROR:
                cellContent = "error";
                break;
            default:
                break;
        }
        return cellContent;
    }

    public static Map<String,CellStyle> getRowCellStyle(Workbook workbook){
        // 设置标题行样式
        CellStyle firstRowcellStyle = workbook.createCellStyle();
        firstRowcellStyle.setFillForegroundColor(HSSFColorPredefined.SKY_BLUE.getIndex());// 设置背景色
        firstRowcellStyle.setVerticalAlignment(VerticalAlignment.TOP);// 上下居中
        firstRowcellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        firstRowcellStyle.setAlignment(HorizontalAlignment.CENTER);
        firstRowcellStyle.setBorderBottom(BorderStyle.THIN);
        firstRowcellStyle.setBorderLeft(BorderStyle.THIN);
        firstRowcellStyle.setBorderRight(BorderStyle.THIN);
        firstRowcellStyle.setBorderTop(BorderStyle.THIN);
        Font font = workbook.createFont();
        font.setFontName("宋体");
        font.setFontHeight((short) 220);
        font.setBold(true);
        firstRowcellStyle.setFont(font);
        //设置正文行样式
        CellStyle rowcellStyle = workbook.createCellStyle();
        rowcellStyle.setFillForegroundColor(HSSFColorPredefined.LIGHT_YELLOW.getIndex());// 设置背景色
        rowcellStyle.setVerticalAlignment(VerticalAlignment.TOP);// 上下居中
        rowcellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        rowcellStyle.setAlignment(HorizontalAlignment.CENTER);
        rowcellStyle.setBorderBottom(BorderStyle.THIN);
        rowcellStyle.setBorderLeft(BorderStyle.THIN);
        rowcellStyle.setBorderRight(BorderStyle.THIN);
        rowcellStyle.setBorderTop(BorderStyle.THIN);
        rowcellStyle.setFont(font);
        Map<String,CellStyle> cellStyleMap = new HashMap<>(2);
        cellStyleMap.put("firstRowcellStyle",firstRowcellStyle);
        cellStyleMap.put("rowcellStyle",rowcellStyle);
        return cellStyleMap;
    }


}
