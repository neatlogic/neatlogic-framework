/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package neatlogic.framework.util.word;

import neatlogic.framework.util.word.enums.TableColor;
import neatlogic.framework.util.word.enums.TitleType;
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
     * @param tableColor     表头颜色
     * @return TableBuilder
     */
    public TableBuilder addTable(Map<Integer, String> tableHeaderMap, TableColor tableColor) {
        XWPFTable table = document.createTable();
        return new TableBuilder(table, tableHeaderMap, tableColor);
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
