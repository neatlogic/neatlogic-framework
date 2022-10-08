/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */
package codedriver.framework.util.word;

import codedriver.framework.util.word.enums.TitleType;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import java.io.IOException;
import java.util.Map;

/**
 * @author longrf
 * @date 2022/9/23 15:55
 */

public class WordBuilder {

    private XWPFDocument document;

    public WordBuilder() throws IOException {
        this.document = new XWPFDocument();
    }

    public WordBuilder(String fileUrl) throws IOException {
        this.document = new XWPFDocument(POIXMLDocument.openPackage(fileUrl));
    }

    public XWPFDocument builder() {
        return document;
    }

    /**
     * 添加题目、标题
     *
     * @param titleType 标题类型
     * @param titleName 标题名称
     * @return XWPFDocument
     */
    public TitleBuilder addTitle(TitleType titleType, String titleName) {
        return new TitleBuilder(document, titleType, titleName);
    }

    /**
     * 添加表格
     *
     * @param tableHeaderMap 表头
     * @return TableBuilder
     */
    public TableBuilder addTable(Map<Integer, String> tableHeaderMap) {
        XWPFTable table = document.createTable();
        return new TableBuilder(table, tableHeaderMap);
    }

    /**
     * 添加表格
     *
     * @return TableBuilder
     */
    public TableBuilder addTable() {
        XWPFTable table = document.createTable();
        return new TableBuilder(table);
    }

    /**
     * 添加空白段落
     *
     * @return ParagraphBuilder
     */
    public ParagraphBuilder addParagraph() {
        XWPFParagraph xwpfParagraph = document.createParagraph();
        return new ParagraphBuilder(xwpfParagraph);

    }

    /**
     * 添加段落
     *
     * @return ParagraphBuilder
     */
    public ParagraphBuilder addParagraph(String paragraph) {
        XWPFParagraph xwpfParagraph = document.createParagraph();
        return new ParagraphBuilder(xwpfParagraph, paragraph);
    }

    /**
     * 添加空白行
     *
     * @return WordBuilder
     */
    public WordBuilder addBlankRow() {
        XWPFParagraph xwpfParagraph = document.createParagraph();
        xwpfParagraph.createRun();
        return this;
    }
}
