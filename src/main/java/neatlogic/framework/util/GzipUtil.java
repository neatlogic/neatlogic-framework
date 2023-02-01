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

package neatlogic.framework.util;

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
