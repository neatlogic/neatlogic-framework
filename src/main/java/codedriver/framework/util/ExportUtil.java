package codedriver.framework.util;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
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
import org.w3c.tidy.Tidy;

import javax.xml.bind.JAXBElement;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ExportUtil {

    /**
     * 导出HTML为PDF
     *
     * @param html                html
     * @param os                  输出流
     * @param landscape
     * @param isNeedCompletedHtml 是否需要补全HTML标签
     * @throws Exception
     */
    public static void getPdfFileByHtml(String html, OutputStream os, boolean landscape, boolean isNeedCompletedHtml) throws Exception {
        html = html.replaceAll("(?!\\\"|\\&amp;)&nbsp;(?!\\\")", " ");
        if (isNeedCompletedHtml) {
            String completedHtml = completeHtml(html);
            if (StringUtils.isNotBlank(completedHtml)) {
                html = completedHtml;
            }
        }
        html = html.replaceAll("[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]", "");// 过滤掉XML的无效字符
        Document doc = Jsoup.parse(html);
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml).escapeMode(Entities.EscapeMode.xhtml); // 转为
        savePdf(xhtml2word(doc, landscape), os);
    }

    /**
     * 导出HTML为WORD
     *
     * @param html                html
     * @param os                  输出流
     * @param landscape
     * @param isNeedCompletedHtml 是否需要补全HTML标签
     * @throws Exception
     */
    public static void getWordFileByHtml(String html, OutputStream os, boolean landscape, boolean isNeedCompletedHtml) throws Exception {
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
        saveDocx(xhtml2word(doc, landscape), os);
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
        com.itextpdf.text.Document doc = new com.itextpdf.text.Document(landscape ? PageSize.A4 : PageSize.A4.rotate());
        PdfWriter writer = PdfWriter.getInstance(doc, os);
        doc.open();
        ByteArrayInputStream bis = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        XMLWorkerHelper.getInstance().parseXHtml(writer, doc, bis, StandardCharsets.UTF_8);
        bis.close();
        doc.close();
        writer.close();
    }

}
