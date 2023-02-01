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
