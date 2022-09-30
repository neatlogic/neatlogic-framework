package codedriver.framework.util.word;

import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.xwpf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通过word模板生成新的word工具类
 *
 * @author zhiheng
 */
public class WordToNewWordUtil {

    /**
     * 根据模板生成新word文档
     * 判断表格是需要替换还是需要插入，判断逻辑有$为替换，表格无$为插入
     *
     * @param inputUrl  模板存放地址
     * @param outPutUrl 新文档存放地址
     * @param textMap   需要替换的信息集合
     * @param tableList 需要插入的表格信息集合
     * @return 成功返回true, 失败返回false
     */
    public static boolean changWord(String inputUrl, String outPutUrl,
                                    Map<String, String> textMap, List<String[]> tableList) {

        //模板转换默认成功
        boolean changeFlag = true;
        try {
            //获取docx解析对象
            XWPFDocument document = new XWPFDocument(POIXMLDocument.openPackage(inputUrl));
            //解析替换文本段落对象
            WordToNewWordUtil.changeText(document, textMap);
            //解析替换表格对象
            WordToNewWordUtil.changeTable(document, textMap);

            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(getMapValue("name", textMap), 0);

//            XWPFTable table = document.createTable();
            for (XWPFTable table1 : document.getTables()) {
                System.out.println(table1.getBody());
                System.out.println(table1.getText());
                System.out.println(table1.getBottomBorderColor());
                System.out.println(table1.getBottomBorderSize());
//                System.out.println(table1.getCTTbl());

            }
            //生成新的word
            File file = new File(outPutUrl);
            FileOutputStream stream = new FileOutputStream(file);
            document.write(stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
            changeFlag = false;
        }

        return changeFlag;

    }

    /**
     * 替换段落文本
     *
     * @param document docx解析对象
     * @param textMap  需要替换的信息集合
     */
    public static void changeText(XWPFDocument document, Map<String, String> textMap) {
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            //判断此段落时候需要进行替换
            if (paragraph.getText().contains("$")) {
                List<XWPFRun> runs = paragraph.getRuns();
                for (XWPFRun run : runs) {
                    //替换模板原来位置
                    run.setText(getMapValue(run.toString(), textMap), 0);
                }
            }
        }
    }

    /**
     * 替换表格对象方法
     *
     * @param document  docx解析对象
     * @param textMap   需要替换的信息集合
     */
    public static void changeTable(XWPFDocument document, Map<String, String> textMap) {


//        if (!value.startsWith("${") || !value.endsWith("}")) {
//            return value;
//        }
//        return textMap.get(value.substring(2, value.length() - 1));



        //获取表格对象集合
        List<XWPFTable> tables = document.getTables();
        for (XWPFTable table : tables) {
            //只处理行数大于等于2的表格，且不循环表头
            if (table.getRows().size() > 1) {
                //判断表格是需要替换还是需要插入，判断逻辑有$为替换，表格无$为插入
                if (table.getText().contains("$")) {
                    List<XWPFTableRow> rows = table.getRows();
                    //遍历表格,并替换模板
                    updateTable(rows, textMap);
                }
            }
        }
    }


    /**
     * 遍历表格
     *
     * @param rows    表格行对象
     * @param textMap 需要替换的信息集合
     */
    public static void updateTable(List<XWPFTableRow> rows, Map<String, String> textMap) {
        for (XWPFTableRow row : rows) {
            for (XWPFTableCell cell : row.getTableCells()) {
                //判断单元格是否需要替换
                if (cell.getText().contains("$")) {
                    for (XWPFParagraph paragraph :  cell.getParagraphs()) {
                        for (XWPFRun run :  paragraph.getRuns()) {
                            run.setText(getMapValue(run.toString(), textMap), 0);
                        }
                    }
                }
            }
        }
    }

    /**
     * 为表格插入数据，行数不够添加新行
     *
     * @param table     需要插入数据的表格
     * @param tableList 插入数据集合
     */
    public static void insertTable(XWPFTable table, List<String[]> tableList) {
        //创建行,根据需要插入的数据添加新行，不处理表头
        for (int i = 1; i < tableList.size(); i++) {
            XWPFTableRow row = table.createRow();
        }
        //遍历表格插入数据
        List<XWPFTableRow> rows = table.getRows();
        for (int i = 1; i < rows.size(); i++) {
            XWPFTableRow newRow = table.getRow(i);
            List<XWPFTableCell> cells = newRow.getTableCells();
            for (int j = 0; j < cells.size(); j++) {
                XWPFTableCell cell = cells.get(j);
                cell.setText(tableList.get(i - 1)[j]);
            }
        }

    }

    /**
     * 匹配传入信息集合与模板
     *
     * @param value   模板需要替换的区域
     * @param textMap 传入信息集合
     * @return 模板需要替换区域信息集合对应值
     */
    public static String getMapValue(String value, Map<String, String> textMap) {
        if (!value.startsWith("${") || !value.endsWith("}")) {
            return value;
        }
        return textMap.get(value.substring(2, value.length() - 1));
    }


    public static void main(String[] args) {
        //模板文件地址
        String inputUrl = "/Users/longrf/Desktop/out001.docx";
        //新生产的模板文件
        String outputUrl = "/Users/longrf/Desktop/out001.docx";

        Map<String, String> testMap = new HashMap<String, String>();
        testMap.put("name", "小明");
        testMap.put("sex", "男");
        testMap.put("address", "软件园");
        testMap.put("phone", "88888888");
        testMap.put("age", "43333333333333333333333333333");
        testMap.put("like", "gsgbv");

        List<String[]> testList = new ArrayList<String[]>();
        testList.add(new String[]{"1", "1AA", "1BB", "1CC"});
        testList.add(new String[]{"2", "2AA", "2BB", "2CC"});
        testList.add(new String[]{"3", "3AA", "3BB", "3CC"});
        testList.add(new String[]{"4", "4AA", "4BB", "4CC"});

        WordToNewWordUtil.changWord(inputUrl, outputUrl, testMap, testList);
//        try {
//            int i = 1;
//            WordBuilder wordBuilder = new WordBuilder("/Users/longrf/Desktop/002.docx");
//            //生成新的word
//            File file = new File("/Users/longrf/Desktop/002.docx");
//            FileOutputStream stream = new FileOutputStream(file);
//            wordBuilder.builder().write(stream);
//            stream.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}
