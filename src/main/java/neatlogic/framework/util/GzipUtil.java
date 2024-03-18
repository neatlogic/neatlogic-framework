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

package neatlogic.framework.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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
