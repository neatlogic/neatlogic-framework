/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */
package codedriver.framework.util.word;

import codedriver.framework.util.word.enums.TitleType;
import codedriver.framework.util.word.table.TableBuilder;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.IOException;
import java.math.BigInteger;

/**
 * @author longrf
 * @date 2022/9/23 15:55
 */

public class WordBuilder {

    //文件
    private XWPFDocument document;
//    //页脚
//    protected List<XWPFFooter> footers = new ArrayList();
//    //标题
//    protected List<XWPFHeader> headers = new ArrayList();
//    //评论
//    protected List<XWPFComment> comments = new ArrayList();
//    //超链接
//    protected List<XWPFHyperlink> hyperlinks = new ArrayList();
//    //段落
//    protected List<XWPFParagraph> paragraphs = new ArrayList();
//    //表格
//    protected List<XWPFTable> tables = new ArrayList();
//    //内容控件
//    protected List<XWPFSDT> contentControls = new ArrayList();
//    //body元素
//    protected List<IBodyElement> bodyElements = new ArrayList();
//    //图片
//    protected List<XWPFPictureData> pictures = new ArrayList();
//    //包装图片
//    protected Map<Long, List<XWPFPictureData>> packagePictures = new HashMap();
//    //尾注
//    protected XWPFEndnotes endnotes;
//    //编号
//    protected XWPFNumbering numbering;
//    //样式
//    protected XWPFStyles styles;
//    //脚注
//    protected XWPFFootnotes footnotes;
//    private CTDocument1 ctDocument;
//    private XWPFSettings settings;
//    protected final List<XWPFChart> charts = new ArrayList();
//    private IdentifierManager drawingIdManager = new IdentifierManager(0L, 4294967295L);
//    private FootnoteEndnoteIdManager footnoteIdManager = new FootnoteEndnoteIdManager(document);
//    private XWPFHeaderFooterPolicy headerFooterPolicy;


    public WordBuilder() throws IOException {
        this.document = new XWPFDocument();
    }

    public WordBuilder(String fileUrl) throws IOException {
        this.document = new XWPFDocument(POIXMLDocument.openPackage(fileUrl));
    }

    public XWPFDocument builder() {
        return document;
    }

//    public WordBuilder addTable(XWPFTable xwpfTable) {
//        XWPFTable table = document.get;
//        table = xwpfTable;
//        CTTblPr tblPr = table.getCTTbl().getTblPr();
//
//        tblPr.getTblW().setType(STTblWidth.DXA);
//
//        tblPr.getTblW().setW(new BigInteger("8310"));
//        return null;
//    }

    /**
     * 添加题目、标题
     *
     * @param titleType 标题类型
     * @param titleName 标题名称
     * @return XWPFDocument
     */
    public XWPFDocument addTitle(TitleType titleType, String titleName) {
        createXWPFTitle(document, titleType, titleName);
        return document;
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
     * 添加段落
     *
     * @return ParagraphBuilder
     */
    public ParagraphBuilder addParagraph() {
        XWPFParagraph xwpfParagraph = document.createParagraph();
        return new ParagraphBuilder(xwpfParagraph);

    }

    /**
     * 添加空白行
     *
     * @return WordBuilder
     */
    public WordBuilder addBlankRow() {
        XWPFParagraph xwpfParagraph = document.createParagraph();
        xwpfParagraph.createRun();
//        run.addCarriageReturn();//回车键
        return this;
    }

    /**
     * 添加标题
     *
     * @param docxDocument 文件
     * @param titleType    标题类型
     * @param titleName    标题名称
     */
    private static void createXWPFTitle(XWPFDocument docxDocument, TitleType titleType, String titleName) {
        XWPFParagraph paragraph = docxDocument.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(titleName);
        run.setTextPosition(35);//设置行间距
        run.setBold(titleType.getBold());//加粗
        run.setColor(titleType.getColor());//设置颜色--十六进制
        run.setFontFamily(titleType.getFontFamily());//字体
        run.setFontSize(titleType.getFontSize());//字体大小
        if (titleType.getValue().equals(TitleType.TILE.getValue())) {
            paragraph.setAlignment(ParagraphAlignment.CENTER);//对齐方式
        } else if (titleType.getValue().equals(TitleType.H1.getValue()) || titleType.getValue().equals(TitleType.H2.getValue()) || titleType.getValue().equals(TitleType.H3.getValue())) {
            addCustomHeadingStyle(docxDocument, titleType.getText(), titleType.getHeadingLevel());
            paragraph.setStyle(titleType.getText());
        }
    }


    /**
     * 增加自定义标题样式。这里用的是stackoverflow的源码
     *
     * @param docxDocument 目标文档
     * @param strStyleId   样式名称
     * @param headingLevel 样式级别
     */
    private static void addCustomHeadingStyle(XWPFDocument docxDocument, String strStyleId, int headingLevel) {

        CTStyle ctStyle = CTStyle.Factory.newInstance();
        ctStyle.setStyleId(strStyleId);

        CTString styleName = CTString.Factory.newInstance();
        styleName.setVal(strStyleId);
        ctStyle.setName(styleName);

        CTDecimalNumber indentNumber = CTDecimalNumber.Factory.newInstance();
        indentNumber.setVal(BigInteger.valueOf(headingLevel));

        // lower number > style is more prominent in the formats bar
        ctStyle.setUiPriority(indentNumber);

        CTOnOff ct = CTOnOff.Factory.newInstance();
        ctStyle.setUnhideWhenUsed(ct);

        // style shows up in the formats bar
        ctStyle.setQFormat(ct);

        // style defines a heading of the given level
        CTPPr ppr = CTPPr.Factory.newInstance();
        ppr.setOutlineLvl(indentNumber);
        ctStyle.setPPr(ppr);

        XWPFStyle style = new XWPFStyle(ctStyle);

        // is a null op if already defined
        XWPFStyles styles = docxDocument.createStyles();

        style.setType(STStyleType.PARAGRAPH);
        styles.addStyle(style);

    }


//    /**
//     * 设置页面大小及纸张方向 landscape横向
//     * @param document
//     * @param width
//     * @param height
//     * @param stValue
//     */
//    public void setDocumentSize(XWPFDocument document, String width,String height, STPageOrientation stValue) {
//        CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
//        CTPages
//        CTPageSz pgsz = sectPr.isSetPgSz() ? sectPr.getPgSz() : sectPr.addNewPgSz();
//        pgsz.setH(new BigInteger(height));
//        pgsz.setW(new BigInteger(width));
//        pgsz.setOrient(stValue);
//    }

//    /**
//     * 设置页边距 (word中1厘米约等于567)
//     * @param document
//     * @param left
//     * @param top
//     * @param right
//     * @param bottom
//     */
//    public static void setDocumentMargin(XWPFDocument document, String left,String top, String right, String bottom) {
//        CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
//       CTPageMar ctPageMar = sectPr.addNewPgMar();
//        if (StringUtils.isNotBlank(left)) {
//            ctpagemar.setLeft(new BigInteger(left));
//        }
//        if (StringUtils.isNotBlank(top)) {
//            ctpagemar.setTop(new BigInteger(top));
//        }
//        if (StringUtils.isNotBlank(right)) {
//            ctpagemar.setRight(new BigInteger(right));
//        }
//        if (StringUtils.isNotBlank(bottom)) {
//            ctpagemar.setBottom(new BigInteger(bottom));
//        }
//    }

}
