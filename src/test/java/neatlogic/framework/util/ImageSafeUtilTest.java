package neatlogic.framework.util;

import org.junit.Test;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.*;

/** 验证图片真实格式、动画完整读取和流生命周期。 */
public class ImageSafeUtilTest {
    /** 正常图片及大小写扩展名必须保留支持。 */
    @Test
    public void acceptsSupportedImages() throws Exception {
        for (String format : Arrays.asList("png", "jpeg", "gif")) {
            byte[] bytes = image(format);
            assertEquals("image/" + format, ImageSafeUtil.validateImage(new ByteArrayInputStream(bytes), "测试." + format.toUpperCase()));
        }
        assertEquals("image/jpeg", ImageSafeUtil.validateImage(new ByteArrayInputStream(image("jpeg")), "C:\\fakepath\\头像.jpg"));
    }

    /** 伪造扩展名、SVG和截断数据均不得通过。 */
    @Test
    public void rejectsMismatchedAndInvalidContent() throws Exception {
        assertRejected(image("png"), "fake.jpg");
        assertRejected("<svg xmlns='http://www.w3.org/2000/svg'/>".getBytes("UTF-8"), "fake.svg");
        assertRejected("not an image".getBytes("UTF-8"), "fake.png");
        byte[] png = image("png");
        assertRejected(Arrays.copyOf(png, png.length / 2), "broken.png");
    }

    /** 校验工具不得关闭调用者的流。 */
    @Test
    public void keepsCallerStreamOpen() throws Exception {
        final boolean[] closed = {false};
        ByteArrayInputStream input = new ByteArrayInputStream(image("png")) {
            @Override
            public void close() {
                closed[0] = true;
            }
        };
        ImageSafeUtil.validateImage(input, "test.png");
        assertFalse(closed[0]);
        input.reset();
        ImageSafeUtil.validateImageContent(input);
        assertFalse(closed[0]);
    }

    /** 动画全部帧可解码，校验不改写原始图片。 */
    @Test
    public void acceptsAnimatedGif() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageWriter writer = ImageIO.getImageWritersByFormatName("gif").next();
        try (ImageOutputStream imageOutput = ImageIO.createImageOutputStream(output)) {
            writer.setOutput(imageOutput);
            writer.prepareWriteSequence(null);
            for (int i = 0; i < 2; i++) {
                BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
                image.setRGB(0, 0, i * 0xffffff);
                writer.writeToSequence(new IIOImage(image, null, null), null);
            }
            writer.endWriteSequence();
        } finally {
            writer.dispose();
        }
        assertEquals("image/gif", ImageSafeUtil.validateImage(new ByteArrayInputStream(output.toByteArray()), "animated.gif"));
        assertEquals("image/gif", ImageSafeUtil.validateImageContent(new ByteArrayInputStream(output.toByteArray())));
    }

    /** 历史下载只按实际格式确认MIME，上传仍严格匹配扩展名。 */
    @Test
    public void acceptsContentWithoutDependingOnFileExtension() throws Exception {
        for (String format : Arrays.asList("png", "jpeg", "gif")) {
            assertEquals("image/" + format, ImageSafeUtil.validateImageContent(new ByteArrayInputStream(image(format))));
        }
        assertRejected(image("jpeg"), "legacy.png");
        assertRejected(image("gif"), "legacy.png");
    }

    /** 仅内容验证同样拒绝SVG、文本、非白名单图片和损坏内容。 */
    @Test
    public void rejectsUnsupportedAndDamagedImageContent() throws Exception {
        byte[] png = image("png");
        byte[][] invalidContents = {
                "<svg xmlns='http://www.w3.org/2000/svg'/>".getBytes("UTF-8"),
                "not an image".getBytes("UTF-8"), image("bmp"),
                Arrays.copyOf(png, png.length / 2)
        };
        for (byte[] bytes : invalidContents) {
            try {
                ImageSafeUtil.validateImageContent(new ByteArrayInputStream(bytes));
                fail("应拒绝不受支持或损坏的图片");
            } catch (IOException expected) {
                assertNotNull(expected.getMessage());
            }
        }
    }

    /** 生成独立测试图片，不读取业务文件。 */
    private byte[] image(String format) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        assertTrue(ImageIO.write(new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB), format, output));
        return output.toByteArray();
    }

    /** 确认危险或无效内容抛出校验异常。 */
    private void assertRejected(byte[] bytes, String name) throws Exception {
        try {
            ImageSafeUtil.validateImage(new ByteArrayInputStream(bytes), name);
            fail("应拒绝无效图片：" + name);
        } catch (IOException expected) {
            assertNotNull(expected.getMessage());
        }
    }
}
