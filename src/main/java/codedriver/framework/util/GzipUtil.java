/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.*;

public class GzipUtil {
    private static final Logger logger = LoggerFactory.getLogger(GzipUtil.class);


    public static String compress(String primStr) {
        if (StringUtils.isBlank(primStr)) {
            return primStr;
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(primStr.getBytes(StandardCharsets.UTF_8));
            gzip.close();
            return Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return primStr;
        }
    }


    /**
     * 使用gzip进行解压缩
     */
    public static String uncompress(String compressedStr) {
        if (StringUtils.isBlank(compressedStr)) {
            return compressedStr;
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ByteArrayInputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(compressedStr.getBytes()))) {
            GZIPInputStream unzip = new GZIPInputStream(in);
            byte[] buffer = new byte[1024];
            int n;
            while ((n = unzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }

            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return compressedStr;
        }
    }

}
