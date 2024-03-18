/*Copyright (C) 2023  深圳极向量科技有限公司 All Rights Reserved.

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

import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * 下载文件工具类
 *
 * @author linbq
 * @since 2021/4/29 11:17
 **/
public class FileUtil {
    /**
     * chrome、firefox、edge浏览器下载文件时，文件名包含~@#$&+=;这八个英文字符时会变成乱码_%40%23%24%26%2B%3D%3B，
     * 下面是对@#$&+=;这七个字符做特殊处理，
     * 对于~这个字符还是会变成下划线_，暂无法处理
     *
     * @param fileName 文件名
     * @return 返回处理后的文件名
     */
    public static String fileNameSpecialCharacterHandling(String fileName) {
        if (fileName.contains("%40")) {
            fileName = fileName.replace("%40", "@");
        }
        if (fileName.contains("%23")) {
            fileName = fileName.replace("%23", "#");
        }
        if (fileName.contains("%24")) {
            fileName = fileName.replace("%24", "$");
        }
        if (fileName.contains("%26")) {
            fileName = fileName.replace("%26", "&");
        }
        if (fileName.contains("%2B")) {
            fileName = fileName.replace("%2B", "+");
        }
        if (fileName.contains("%3D")) {
            fileName = fileName.replace("%3D", "=");
        }
        if (fileName.contains("%3B")) {
            fileName = fileName.replace("%3B", ";");
        }
        return fileName;
    }

    /**
     * Firefox浏览器userAgent：Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:88.0) Gecko/20100101 Firefox/88.0
     * Chrome浏览器userAgent：Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.85 Safari/537.36
     * Edg浏览器userAgent：Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.85 Safari/537.36 Edg/90.0.818.46
     *
     * @param fileName
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getEncodedFileName(String fileName) throws UnsupportedEncodingException {
//        fileName = URLEncoder.encode(fileName, "UTF-8");
//        fileName = fileNameSpecialCharacterHandling(fileName);
//        return fileName;
        return UriComponentsBuilder.fromUriString(fileName).build().encode().toUri().toString();
    }

    /**
     * 读取文件内容
     *
     * @param filePath 文件路径
     * @return 文件内容
     */
    public static String getReadFileContent(String filePath) {
        if (filePath == null || "".equals(filePath)) {
            return null;
        } else {
            String result = "";
            FileInputStream fr = null;
            BufferedReader filebr = null;
            InputStreamReader in = null;
            File desFile = new File(filePath);
            try {
                if (desFile.isFile() && desFile.exists()) {
                    StringBuilder str = new StringBuilder();
                    fr = new FileInputStream(desFile);
                    in = new InputStreamReader(fr, StandardCharsets.UTF_8);
                    filebr = new BufferedReader(in);
                    String inLine = "";
                    while ((inLine = filebr.readLine()) != null) {
                        str.append(inLine);
                    }
                    result = str.toString();
                } else {
                    result = "";
                }
            } catch (Exception ex) {
                result = ex.getMessage();
            } finally {
                if (fr != null) {
                    try {
                        fr.close();
                    } catch (IOException ignored) {
                    }
                }
                if (filebr != null) {
                    try {
                        filebr.close();
                    } catch (IOException ignored) {
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ignored) {

                    }
                }
            }
            return result;
        }
    }
}
