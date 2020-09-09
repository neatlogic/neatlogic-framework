package codedriver.framework.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * @program: codedriver
 * @description:
 * @create: 2020-03-31 17:00
 **/
public class ExcelUtil {

    /** 
    * @Description: excel 导出 
    * @Param: [headerList, columnList, columnSelectValueList, dataMapList, os] 
    * @return: void  
    */ 
    public static void exportExcel(List<String> headerList, List<String> columnList, List<List<String>> columnSelectValueList, List<Map<String, String>> dataMapList, OutputStream os)
    {
        HSSFWorkbook workbook = new HSSFWorkbook();
        workbook.createSheet("sheet01");

        // 声明一个工作薄   生成一个表格
        HSSFSheet sheet = workbook.getSheet("sheet01");
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth((short) 15);
        // 生成一个样式
        HSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 生成一个字体
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.VIOLET.index);
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        style.setFont(font);
        // 生成并设置另一个样式
        HSSFCellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
        style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        // 生成另一个字体
        HSSFFont font2 = workbook.createFont();
        font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        style2.setFont(font2);
        
        HSSFFont font3 = workbook.createFont();
        font3.setColor(HSSFColor.BLUE.index);
        
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
            style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
            style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            // 生成一个字体
            HSSFFont font = workbook.createFont();
            font.setColor(HSSFColor.VIOLET.index);
            font.setFontHeightInPoints((short) 12);
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
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
            style2.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
            style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
            style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            // 生成另一个字体
            HSSFFont font2 = workbook.createFont();
            font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
            // 把字体应用到当前的样式
            style2.setFont(font2);
        	HSSFFont font3 = workbook.createFont();
            font3.setColor(HSSFColor.BLUE.index);
            
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
     * 使用poi-3.8的SXSSFWorkbook来创建excel
     * 3.8以前的版本，单元格字符数有限
     * @param workbook
     * @param headerList
     * @param columnList
     * @param dataMapList
     * @return
     * @throws Exception
     */
    public static SXSSFWorkbook exportData(SXSSFWorkbook workbook, List<String> headerList, List<String> columnList, List<Map<String,Object>> dataMapList,Integer columnWidth) throws Exception {
        // 生成一个表格
        Sheet sheet = workbook.createSheet();
        // 设置sheet名字
        workbook.setSheetName(0,"sheet01");
        // 设置标题行样式
        CellStyle firstRowcellStyle = workbook.createCellStyle();
        firstRowcellStyle.setFillForegroundColor(HSSFColor.SKY_BLUE.index);// 设置背景色
        firstRowcellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);// 上下居中
        firstRowcellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        firstRowcellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        firstRowcellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        firstRowcellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        firstRowcellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        firstRowcellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        Font font = workbook.createFont();
        font.setFontName("宋体");
        font.setFontHeight((short) 220);
        font.setBoldweight((short) 700);
        firstRowcellStyle.setFont(font);
        //设置正文行样式
        CellStyle rowcellStyle = workbook.createCellStyle();
        rowcellStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);// 设置背景色
        rowcellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);// 上下居中
        rowcellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        rowcellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        rowcellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        rowcellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        rowcellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        rowcellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        rowcellStyle.setFont(font);

        //生成标题行
        Row headerRow = sheet.createRow(0);
        if(CollectionUtils.isNotEmpty(headerList)){
            int i = 0;
            for (String header : headerList) {
                //设置列宽
                if(columnWidth != null && columnWidth > 0){
                    sheet.setColumnWidth(i,columnWidth.intValue() * 256);
                }
                Cell cell = headerRow.createCell(i);
                cell.setCellStyle(firstRowcellStyle);
//                HSSFRichTextString text = new HSSFRichTextString(header);
                cell.setCellValue(header);
                i++;
            }
        }
        //生成数据行
        if(CollectionUtils.isNotEmpty(columnList) && CollectionUtils.isNotEmpty(dataMapList)){
            int lastRowNum = sheet.getLastRowNum();
            for (Map<String, Object> dataMap : dataMapList){
                lastRowNum++;
                Row row = sheet.createRow(lastRowNum);
                int j = 0;
                for (String column : columnList){
                    Cell cell = row.createCell(j);
                    cell.setCellStyle(rowcellStyle);
//                    HSSFRichTextString richString = new HSSFRichTextString(dataMap.get(column) == null ? "" : dataMap.get(column).toString());
                    cell.setCellValue(dataMap.get(column) == null ? null : dataMap.get(column).toString());
                    j++;
                }
            }
        }
        return workbook;
    }
}