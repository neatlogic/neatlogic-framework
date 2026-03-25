package neatlogic.framework.util;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.Docx4J;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.jaxb.Context;
import org.docx4j.model.structure.PageSizePaper;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.jsoup.nodes.Element;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.xml.bind.JAXBElement;
import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ExportUtil {

    /**
     * 导出HTML为PDF
     *
     * @param html                html
     * @param os                  输出流
     * @param landscape           是否竖向排版
     * @param isNeedCompletedHtml 是否需要补全HTML标签
     * @throws Exception
     */
    public static void getPdfFileByHtml(String html, OutputStream os, boolean landscape, boolean isNeedCompletedHtml) throws Exception {
        html = html.replaceAll("(?!\\\"|\\&amp;)&nbsp;(?!\\\")", " ");
        savePdf(getWordprocessingMLPackage(html, landscape, isNeedCompletedHtml), os);
    }

    /**
     * 通过轻量直出链路将 HTML 导出为 PDF，供新报表导出接口使用。
     *
     * @param html 原始 HTML 内容
     * @param os 目标输出流
     * @param landscape 是否使用横向页面
     * @param isNeedCompletedHtml 渲染前是否需要通过 Tidy 补全 HTML
     * @return PDF 导出各阶段的耗时指标
     * @throws Exception 当 HTML 规范化或 PDF 渲染失败时抛出异常
     */
    public static void getPdfFileByHtmlFast(String html, OutputStream os, boolean landscape, boolean isNeedCompletedHtml) throws Exception {
        // 新报表导出走轻量直出路径，先把 HTML 规范成适合 PDF 渲染的 XHTML。
        String normalizedHtml = normalizeHtmlForPdf(html, landscape, isNeedCompletedHtml);
        ITextRenderer renderer = createPdfRenderer(normalizedHtml);
        renderer.layout();
        renderer.createPDF(os);
    }

    /**
     * 导出HTML为WORD
     *
     * @param html                html
     * @param os                  输出流
     * @param landscape           是否竖向排版
     * @param isNeedCompletedHtml 是否需要补全HTML标签
     * @throws Exception
     */
    public static void getWordFileByHtml(String html, OutputStream os, boolean landscape, boolean isNeedCompletedHtml) throws Exception {
        saveDocx(getWordprocessingMLPackage(html, landscape, isNeedCompletedHtml), os);
    }

    /**
     * @param html                html
     * @param landscape           是否竖向排版
     * @param isNeedCompletedHtml 是否需要补全HTML标签
     * @return
     * @throws Exception
     */
    private static WordprocessingMLPackage getWordprocessingMLPackage(String html, boolean landscape, boolean isNeedCompletedHtml) throws Exception {
        // fixme --laiwt 有部分html经过tidy解析后，返回空串，暂不清楚是何原因
        if (isNeedCompletedHtml) {
            String completedHtml = completeHtml(html);
            if (StringUtils.isNotBlank(completedHtml)) {
                html = completedHtml;
            }
        }
        html = html.replaceAll("[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]", "");// 过滤掉XML的无效字符
        Document doc = Jsoup.parse(html);
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml).escapeMode(Entities.EscapeMode.xhtml); // 转为
        return xhtml2word(doc, landscape);
    }

    /**
     * @Description: 补全HTML标签
     * @Author: laiwt
     * @Date: 2021/3/19 11:19
     * @Params: [html]
     * @Returns: java.lang.String
     **/
    private static String completeHtml(String html) throws IOException {
        Tidy tidy = new Tidy();
        tidy.setXHTML(true);
        tidy.setInputEncoding("utf8");
        tidy.setTabsize(4);
        tidy.setShowWarnings(false);
        tidy.setPrintBodyOnly(false);
        tidy.setSmartIndent(true);
        tidy.setOutputEncoding("utf8");
        StringReader sr = new StringReader(html);
        StringWriter sw = new StringWriter();
        tidy.parse(sr, sw);
        sr.close();
        sw.close();
        return sw.toString();
    }

    /**
     * 在直接渲染 PDF 之前规范化导出 HTML。
     *
     * @param html 原始 HTML 内容
     * @param landscape 是否使用横向页面
     * @param isNeedCompletedHtml 渲染前是否需要通过 Tidy 补全 HTML
     * @return 已补充分页样式的规范化 XHTML 内容
     * @throws IOException 当 HTML 补全过程失败时抛出异常
     */
    private static String normalizeHtmlForPdf(String html, boolean landscape, boolean isNeedCompletedHtml) throws IOException {
        html = html.replaceAll("(?!\\\"|\\&amp;)&nbsp;(?!\\\")", " ");
        if (isNeedCompletedHtml) {
            String completedHtml = completeHtml(html);
            if (StringUtils.isNotBlank(completedHtml)) {
                html = completedHtml;
            }
        }
        html = html.replaceAll("[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]", "");
        Document doc = Jsoup.parse(html);
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml).escapeMode(Entities.EscapeMode.xhtml).prettyPrint(false);
        appendPdfPageStyle(doc, landscape);
        return doc.html();
    }

    /**
     * 向规范化后的导出文档追加页面尺寸样式。
     *
     * @param doc 已解析的 HTML 文档
     * @param landscape 是否使用横向页面
     */
    private static void appendPdfPageStyle(Document doc, boolean landscape) {
        Element head = doc.head();
        if (head == null) {
            head = doc.prependElement("head");
        }
        Element style = head.appendElement("style");
        style.attr("type", "text/css");
        style.appendText(landscape ? "@page { size: A4 landscape; }" : "@page { size: A4; }");
    }

    public static WordprocessingMLPackage xhtml2word(Document doc, boolean landscape) throws Exception {
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage(PageSizePaper.A4, landscape);
        // 配置中文字体
        configSimSunFont(wordMLPackage);

        XHTMLImporterImpl xhtmlImporter = new XHTMLImporterImpl(wordMLPackage);
        wordMLPackage.getMainDocumentPart().getContent().addAll(xhtmlImporter.convert(doc.html(), doc.baseUri()));
        // 配置表格样式
        // configTableStyle(wordMLPackage);

        return wordMLPackage;
    }

    private static void configTableStyle(WordprocessingMLPackage wordMLPackage) throws Exception {
        List<Tbl> tblList = getAllTbl(wordMLPackage);
        int totalWidth = getTotalWidth(wordMLPackage);
        for (int i = 0; i < tblList.size(); i++) {
            Tbl tbl = tblList.get(i);
            int columnCount = getMaxColumn(tbl);
//            setTableWidth(tbl, totalWidth);
            setTableColumnWidth(tbl, columnCount, totalWidth);
//            setTblContentWidth(tbl, columnCount, totalWidth);
        }

    }

    public static void setTblContentWidth(Tbl tbl, int column, int totalWidth) throws Exception {

        List<Tr> trList = getTblAllTr(tbl);
        for (Tr tr : trList) {
            List<Tc> tcList = getTrAllCell(tr);
            for (Tc tc : tcList) {
                setTcWidth(tc, totalWidth / column);
            }
        }
    }

    private static List<Tc> getTrAllCell(Tr tr) {
        List<Object> objList = getAllElementFromObject(tr, Tc.class);
        List<Tc> tcList = new ArrayList<Tc>();
        for (Object tcObj : objList) {
            if (tcObj instanceof Tc) {
                Tc objTc = (Tc) tcObj;
                tcList.add(objTc);
            }
        }
        return tcList;
    }

    private static List<Tr> getTblAllTr(Tbl tbl) {
        List<Object> objList = getAllElementFromObject(tbl, Tr.class);
        List<Tr> trList = new ArrayList<Tr>();
        for (Object obj : objList) {
            if (obj instanceof Tr) {
                Tr tr = (Tr) obj;
                trList.add(tr);
            }
        }
        return trList;

    }

    public static int getMaxColumn(Tbl tbl) {
        List<Object> contentList = tbl.getContent();
        int subColumn = 1;
        for (Object object : contentList) {
            Tr tr = (Tr) object;
            List<Object> trContentList = tr.getContent();
            for (Object trObj : trContentList) {
                Tc tc = (Tc) trObj;
                List<Object> tcContentList = tc.getContent();
                for (Object tcObj : tcContentList) {
                    if (tcObj instanceof Tbl) {
                        int columnSize = ((Tbl) tcObj).getTblGrid().getGridCol().size();
                        if (columnSize > subColumn) {
                            subColumn = columnSize;
                        }
                    }
                }
            }
        }

        return tbl.getTblGrid().getGridCol().size() + subColumn;
    }

    private static int getTotalWidth(WordprocessingMLPackage wordMLPackage) {
        return wordMLPackage.getDocumentModel().getSections().get(0).getPageDimensions().getWritableWidthTwips();
    }

    private static TblPr getTablePr(Tbl tbl) {
        TblPr tblPr = tbl.getTblPr();
        if (tblPr == null) {
            tblPr = new TblPr();
        }
        return tblPr;
    }

    private static TblGrid getTblGrid(Tbl tbl) {

        TblGrid tblPr = tbl.getTblGrid();
        if (tblPr == null) {
            tblPr = new TblGrid();
            tbl.setTblGrid(tblPr);
        }
        return tblPr;
    }

    public static void setTcWidth(Tc tc, int width) {
        if (width > 0) {
            TcPr tcPr = getTcPr(tc);
            tcPr.setNoWrap(new BooleanDefaultTrue() {
                {
                    setVal(false);
                }
            });
            TblWidth tcW = tcPr.getTcW();
            if (tcW == null) {
                tcW = new TblWidth();
                tcPr.setTcW(tcW);
            }
            tcW.setW(BigInteger.valueOf(width));
            tcW.setType("dxa");

        }
    }

    private static void setTableColumnWidth(Tbl tbl, int column, Integer width) {
        TblGrid tblGrid = getTblGrid(tbl);
        List<TblGridCol> columnList = tblGrid.getGridCol();
        int columnCount = columnList.size() - 1 <= 0 ? 1 : columnList.size() - 1;
        BigInteger columnWidth = BigInteger.valueOf(width / columnCount);

        TblPr tblPr = getTablePr(tbl);
        CTTblLayoutType layout = new CTTblLayoutType();
        layout.setType(STTblLayoutType.AUTOFIT);
        tblPr.setTblLayout(layout);
        CTTblOverlap tbloverlap = new CTTblOverlap();
        tbloverlap.setVal(STTblOverlap.NEVER);
        tblPr.setTblOverlap(tbloverlap);
        tbl.setTblPr(tblPr);

        TblWidth tblW = tblPr.getTblW();
        if (tblW == null) {
            tblW = new TblWidth();
        }
        tblW.setW(BigInteger.valueOf(width));
        tblW.setType("dxa");

        for (TblGridCol tblGridCol : columnList) {
            tblGridCol.setW(columnWidth);
        }

        List<Tr> trList = getTblAllTr(tbl);
        for (Tr tr : trList) {
            List<Tc> tcList = getTrAllCell(tr);
            for (Tc tc : tcList) {
                setTcWidth(tc, columnWidth.intValue());
            }
        }

        List<Object> contentList = tbl.getContent();
        for (Object object : contentList) {
            Tr tr = (Tr) object;
            List<Object> trContentList = tr.getContent();
            for (Object trObj : trContentList) {
                Tc tc = (Tc) trObj;
                List<Object> tcContentList = tc.getContent();
                for (Object tcObj : tcContentList) {
                    if (tcObj instanceof Tbl) {
                        setTableColumnWidth((Tbl) tcObj, getTblGrid((Tbl) tcObj).getGridCol().size(), columnWidth.intValue());
                    }
                }
            }
        }

    }

    private static void setTableWidth(Tbl tbl, int width) {
        if (width > 0) {
            TblPr tblPr = getTablePr(tbl);
            TblWidth tblW = tblPr.getTblW();
            if (tblW == null) {
                tblW = new TblWidth();
                tblPr.setTblW(tblW);
            }
            tblW.setW(BigInteger.valueOf(width));
            tblW.setType("dxa");
        }
    }

    public static TcPr getTcPr(Tc tc) {
        TcPr tcPr = tc.getTcPr();
        if (tcPr == null) {
            tcPr = new TcPr();
            tc.setTcPr(tcPr);
        }
        return tcPr;
    }

    private static List<Tbl> getAllTbl(WordprocessingMLPackage wordMLPackage) {
        MainDocumentPart mainDocPart = wordMLPackage.getMainDocumentPart();
        List<Object> objList = getAllElementFromObject(mainDocPart, Tbl.class);
        List<Tbl> tblList = new ArrayList<Tbl>();
        for (Object obj : objList) {
            if (obj instanceof Tbl) {
                Tbl tbl = (Tbl) obj;
                tblList.add(tbl);
            }
        }
        return tblList;
    }

    private static List<Object> getAllElementFromObject(Object obj, Class<?> toSearch) {
        List<Object> result = new ArrayList<>();
        if (obj instanceof JAXBElement)
            obj = ((JAXBElement<?>) obj).getValue();
        if (obj.getClass().equals(toSearch))
            result.add(obj);
        else if (obj instanceof ContentAccessor) {
            List<?> children = ((ContentAccessor) obj).getContent();
            for (Object child : children) {
                result.addAll(getAllElementFromObject(child, toSearch));
            }
        }
        return result;
    }

    private static void configSimSunFont(WordprocessingMLPackage wordMLPackage) throws Exception {
        Mapper fontMapper = new IdentityPlusMapper();
        wordMLPackage.setFontMapper(fontMapper);

        ChineseFont[] fonts = ChineseFont.values();
        for (ChineseFont font : fonts) {
            PhysicalFonts.addPhysicalFont(font.getFontUrl());
            PhysicalFont simsunFont = PhysicalFonts.get(font.getFontName());
            fontMapper.put(font.getFontName(), simsunFont);
        }

        // 设置文件默认字体
        RFonts rfonts = Context.getWmlObjectFactory().createRFonts();
        rfonts.setAsciiTheme(null);
        rfonts.setAscii(ChineseFont.SIMHEI.getFontName());
        RPr rpr = wordMLPackage.getMainDocumentPart().getPropertyResolver().getDocumentDefaultRPr();
        rpr.setRFonts(rfonts);
    }

    private static void savePdf(WordprocessingMLPackage wordMLPackage, OutputStream os) throws FileNotFoundException, Docx4JException {
        Docx4J.toPDF(wordMLPackage, os);
    }

    public static void saveDocx(WordprocessingMLPackage wordMLPackage, OutputStream os) throws Exception {
        wordMLPackage.save(os);
    }

    /**
     * 使用itextpdf导出pdf
     *
     * @param content   html
     * @param os        OutputStream
     * @param landscape 是否竖向排版
     * @throws IOException
     * @throws DocumentException
     */
    public static void savePdf(String content, OutputStream os, boolean landscape) throws IOException, DocumentException {
        ITextRenderer renderer = createPdfRenderer(applyPdfPageStyle(content, landscape));
        renderer.layout();
        renderer.createPDF(os);
    }

    /**
     * 使用缓存字体列表创建 {@link ITextRenderer}。
     *
     * @param content 已可直接用于 PDF 渲染的 XHTML 内容
     * @return 已完成字体配置的 PDF 渲染器
     * @throws IOException 当字体解析器加载字体资源失败时抛出异常
     * @throws DocumentException 当渲染器初始化失败时抛出异常
     */
    private static ITextRenderer createPdfRenderer(String content) throws IOException, DocumentException {
        ITextRenderer renderer = new ITextRenderer();
        ChineseFont[] fonts = ChineseFont.values();
        for (ChineseFont font : fonts) {
            renderer.getFontResolver().addFont(font.getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        }
        renderer.setDocumentFromString(content);
        return renderer;
    }

    /**
     * 向即将直接渲染为 PDF 的 HTML 中注入页面尺寸样式。
     *
     * @param content 原始 HTML 内容
     * @param landscape 是否使用横向页面
     * @return 已注入页面尺寸样式的 HTML 内容
     */
    private static String applyPdfPageStyle(String content, boolean landscape) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        String pageStyle = landscape ? "@page { size: A4 landscape; }" : "@page { size: A4; }";
        String fontStyle = "html,body,div,span,p,table,thead,tbody,tr,th,td,a{font-family:\"SimSun\",\"SimHei\",\"SimKai\",\"SimFang\",\"StFangSo\" !important;}";
        String styleTag = "<style type=\"text/css\">" + pageStyle + fontStyle + "</style>";
        if (content.contains("</head>")) {
            return content.replace("</head>", styleTag + "</head>");
        }
        if (content.contains("<body")) {
            return content.replaceFirst("<body", "<head>" + styleTag + "</head><body");
        }
        return "<head>" + styleTag + "</head>" + content;
    }

}
