package neatlogic.framework.util;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileCacheImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Locale;

/** 图片内容校验，不信任客户端声明的格式及MIME。 */
public final class ImageSafeUtil {
    private ImageSafeUtil() {
    }

    /**
     * 校验扩展名、实际格式和全部图片帧，返回确认的MIME；消费但不关闭调用者流，不重编码图片。
     */
    public static String validateImage(InputStream input, String fileName) throws IOException {
        if (input == null) {
            throw new IOException("图片内容不能为空");
        }
        String name = FileSafeUtil.getUploadFileName(fileName).toLowerCase(Locale.ROOT);
        String expectedFormat;
        if (name.endsWith(".png")) {
            expectedFormat = "png";
        } else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
            expectedFormat = "jpeg";
        } else if (name.endsWith(".gif")) {
            expectedFormat = "gif";
        } else {
            throw new IOException("仅允许PNG、JPEG和GIF图片");
        }
        String contentType = decodeImageContent(input);
        if (!("image/" + expectedFormat).equals(contentType)) {
            throw new IOException("图片扩展名与实际格式不一致");
        }
        return contentType;
    }

    /**
     * 仅按真实内容验证历史图片及文档资源，允许旧文件扩展名与图片格式不同。
     */
    public static String validateImageContent(InputStream input) throws IOException {
        return decodeImageContent(input);
    }

    /** 解码受支持的全部图片帧；消费输入流但不关闭调用者流，也不改写图片数据。 */
    private static String decodeImageContent(InputStream input) throws IOException {
        if (input == null) {
            throw new IOException("图片内容不能为空");
        }
        // ImageInputStream仅释放自身缓存，保护外部流的生命周期。
        try (ImageInputStream imageInput = new FileCacheImageInputStream(new FilterInputStream(input) {
            @Override
            public void close() {
                // 输入流由调用方负责关闭。
            }
        }, null)) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInput);
            if (!readers.hasNext()) {
                throw new IOException("无法识别图片内容");
            }
            ImageReader reader = readers.next();
            try {
                String format = reader.getFormatName().toLowerCase(Locale.ROOT);
                if ("jpg".equals(format)) {
                    format = "jpeg";
                }
                if (!"png".equals(format) && !"jpeg".equals(format) && !"gif".equals(format)) {
                    throw new IOException("仅允许PNG、JPEG和GIF图片");
                }
                reader.setInput(imageInput, false, false);
                int count = reader.getNumImages(true);
                if (count < 1) {
                    throw new IOException("图片不包含可解码内容");
                }
                // GIF逐帧解码以校验动画内容，原始数据仍由调用方原样保存。
                for (int i = 0; i < count; i++) {
                    if (reader.read(i) == null) {
                        throw new IOException("图片内容损坏");
                    }
                }
                return "image/" + format;
            } finally {
                reader.dispose();
            }
        } catch (RuntimeException ex) {
            throw new IOException("无法解码图片内容", ex);
        }
    }
}
