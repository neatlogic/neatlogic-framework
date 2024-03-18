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

import java.io.File;
import java.io.FileInputStream;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author longrf
 * @date 2023/3/17 15:40
 */

public class ZipUtil {

    /***
     * zip()压缩方法
     * @param zipOutputStream   zip的输出流
     * @param inputFile      需要压缩的文件
     * @param fileName          文件名
     */
    public static void zip(ZipOutputStream zipOutputStream, File inputFile, String fileName) throws Exception {
        if (inputFile.isDirectory()) {
            File[] files = inputFile.listFiles();
            if (fileName.length() != 0) {
                zipOutputStream.putNextEntry(new ZipEntry(fileName + "/"));
            }
            for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
                zip(zipOutputStream, files[i], fileName + files[i]);
            }
        } else {
            zipOutputStream.putNextEntry(new ZipEntry(fileName));
            try (FileInputStream fileInputStream = new FileInputStream(inputFile)) {
                int b;
                while ((b = fileInputStream.read()) != -1) {
                    zipOutputStream.write(b);
                }
            }
        }
    }
}
