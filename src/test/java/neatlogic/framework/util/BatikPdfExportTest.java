package neatlogic.framework.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.events.model.EventSeverity;
import org.apache.xmlgraphics.util.MimeConstants;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * 验证 Batik 升级后的 PDF 链路：业务 HTML 导出覆盖 docx4j，内嵌 SVG 覆盖 FOP/Batik。
 * 全部使用本地生成的数据，不连接数据库、业务服务或外部图片地址。
 */
public class BatikPdfExportTest {

    private static Level previousAreaTreeLogLevel;

    /** 关闭 docx4j 将中间 FO 写入工作目录的调试分支，避免测试污染源码目录。 */
    @BeforeClass
    public static void disableFoDebugDump() {
        Logger logger = (Logger) LoggerFactory.getLogger("org.docx4j.convert.out.fo.FOPAreaTreeHelper");
        previousAreaTreeLogLevel = logger.getLevel();
        logger.setLevel(Level.INFO);
    }

    /** 恢复原日志级别，避免影响同一 JVM 中的其他测试。 */
    @AfterClass
    public static void restoreFoDebugLogging() {
        Logger logger = (Logger) LoggerFactory.getLogger("org.docx4j.convert.out.fo.FOPAreaTreeHelper");
        logger.setLevel(previousAreaTreeLogLevel);
    }

    /** 验证业务入口能够导出中文、表格及内嵌 PNG，产物保留供渲染复核。 */
    @Test
    public void exportsHtmlThroughDocx4j() throws Exception {
        Path output = outputPath("html-docx4j.pdf");
        String html = "<html><head><meta charset=\"UTF-8\"/></head><body>"
                + "<h1>PDF Export Verification</h1><p>运维报表：数据库巡检通过</p>"
                + "<table border=\"1\"><tr><th>Asset</th><th>Status</th></tr>"
                + "<tr><td>database-01</td><td>Passed</td></tr></table>"
                + "<p><img width=\"64\" height=\"64\" src=\"" + pngDataUri() + "\"/></p>"
                + "</body></html>";
        try (OutputStream stream = Files.newOutputStream(output)) {
            ExportUtil.getPdfFileByHtml(html, stream, false, false);
        }
        assertPdf(output, "PDF Export Verification", "数据库巡检通过", "database-01", "Passed");
    }

    /** 验证新版报表直出入口不受公共 XML 图形依赖升级影响。 */
    @Test
    public void exportsHtmlThroughFastRenderer() throws Exception {
        Path output = outputPath("html-fast.pdf");
        try (OutputStream stream = Files.newOutputStream(output)) {
            // 快速入口加载字体但不设置默认字体，模板需显式选择已注册的中文字体。
            ExportUtil.getPdfFileByHtmlFast("<html><body style=\"font-family: SimSun\"><h1>Fast PDF Export</h1>"
                    + "<p>巡检结果：正常</p></body></html>", stream, true, false);
        }
        assertPdf(output, "Fast PDF Export", "巡检结果：正常");
    }

    /** 验证 FOP 2.4 与新版 Batik 的实际渲染调用，覆盖渐变、裁剪、内部引用和内嵌图片。 */
    @Test
    public void rendersSvgThroughFop() throws Exception {
        Path output = outputPath("svg-fop.pdf");
        FopFactory factory = FopFactory.newInstance(output.getParent().toUri());
        FOUserAgent agent = factory.newFOUserAgent();
        List<String> errors = new ArrayList<>();
        // FOP 可能记录错误后继续生成空白 PDF，必须额外检查渲染事件。
        agent.getEventBroadcaster().addEventListener(event -> {
            if (event.getSeverity() == EventSeverity.ERROR || event.getSeverity() == EventSeverity.FATAL) {
                errors.add(event.getEventID() + ": " + event.getParams());
            }
        });
        String fo = "<fo:root xmlns:fo=\"http://www.w3.org/1999/XSL/Format\">"
                + "<fo:layout-master-set><fo:simple-page-master master-name=\"page\" "
                + "page-width=\"210mm\" page-height=\"297mm\" margin=\"20mm\">"
                + "<fo:region-body/></fo:simple-page-master></fo:layout-master-set>"
                + "<fo:page-sequence master-reference=\"page\"><fo:flow flow-name=\"xsl-region-body\">"
                + "<fo:block font-size=\"18pt\" space-after=\"16pt\">Batik SVG Verification</fo:block>"
                + "<fo:block><fo:instream-foreign-object>"
                + "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" "
                + "width=\"480\" height=\"240\" viewBox=\"0 0 480 240\">"
                + "<defs><linearGradient id=\"gradient\"><stop offset=\"0\" stop-color=\"#1769aa\"/>"
                + "<stop offset=\"1\" stop-color=\"#32b8a0\"/></linearGradient>"
                + "<clipPath id=\"clip\"><circle cx=\"90\" cy=\"100\" r=\"65\"/></clipPath>"
                + "<path id=\"tick\" d=\"M0 15 L12 27 L36 0\" fill=\"none\" stroke=\"#16833c\" stroke-width=\"6\"/></defs>"
                + "<rect width=\"480\" height=\"240\" fill=\"#f4f7fb\"/>"
                + "<rect x=\"20\" y=\"20\" width=\"150\" height=\"160\" fill=\"url(#gradient)\" clip-path=\"url(#clip)\"/>"
                + "<use xlink:href=\"#tick\" x=\"210\" y=\"55\"/>"
                + "<image x=\"290\" y=\"40\" width=\"64\" height=\"64\" xlink:href=\"" + pngDataUri() + "\"/>"
                + "<text x=\"205\" y=\"145\" font-family=\"sans-serif\" font-size=\"20\">SVG Render OK</text>"
                + "</svg></fo:instream-foreign-object></fo:block></fo:flow></fo:page-sequence></fo:root>";
        try (OutputStream stream = Files.newOutputStream(output)) {
            Fop fop = factory.newFop(MimeConstants.MIME_PDF, agent, stream);
            TransformerFactory.newInstance().newTransformer().transform(
                    new StreamSource(new StringReader(fo)), new SAXResult(fop.getDefaultHandler()));
            assertEquals("SVG 应输出一页", 1, fop.getResults().getPageCount());
        }
        assertTrue("SVG 渲染错误：" + errors, errors.isEmpty());
        assertPdf(output, "Batik SVG Verification");
    }

    /** 生成双色 PNG 数据 URI，避免测试依赖网络或环境中的图片文件。 */
    private static String pngDataUri() throws Exception {
        BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < 64; y++) {
            for (int x = 0; x < 64; x++) {
                image.setRGB(x, y, x < 32 ? 0xffc107 : 0xe53935);
            }
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        assertTrue("PNG 编码器不可用", ImageIO.write(image, "png", stream));
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(stream.toByteArray());
    }

    /** 只在构建目录写入验证产物，允许通过系统属性指定独立输出目录。 */
    private static Path outputPath(String name) throws Exception {
        Path directory = Path.of(System.getProperty("batik.pdf.output", "target/batik-pdf-verification"));
        Files.createDirectories(directory);
        return directory.toAbsolutePath().resolve(name);
    }

    /** 重读生成的 PDF 并核对页数、关键文案，防止仅以转换未抛异常判定成功。 */
    private static void assertPdf(Path path, String... expectedText) throws Exception {
        PdfReader reader = new PdfReader(Files.readAllBytes(path));
        try {
            assertEquals("验证文档应为一页", 1, reader.getNumberOfPages());
            String text = new PdfTextExtractor(reader).getTextFromPage(1);
            for (String expected : expectedText) {
                assertTrue("PDF 缺少文案：" + expected + "；实际：" + text, text.contains(expected));
            }
        } finally {
            reader.close();
        }
    }
}
