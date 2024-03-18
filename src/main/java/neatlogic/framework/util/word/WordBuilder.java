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
