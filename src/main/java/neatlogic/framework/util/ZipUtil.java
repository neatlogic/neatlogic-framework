/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package neatlogic.framework.util;

import org.apache.commons.lang3.StringUtils;

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
            if (StringUtils.isNotBlank(fileName)) {
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
