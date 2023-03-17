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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
     * @throws IOException
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
