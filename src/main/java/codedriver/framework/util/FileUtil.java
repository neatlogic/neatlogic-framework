/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.util;

/**
 * 下载文件工具类
 * @author linbq
 * @since 2021/4/29 11:17
 **/
public class FileUtil {
    /**
     * chrome、firefox、edge浏览器下载文件时，文件名包含~@#$&+=;这八个英文字符时会变成乱码_%40%23%24%26%2B%3D%3B，
     * 下面是对@#$&+=;这七个字符做特殊处理，
     * 对于~这个字符还是会变成下划线_，暂无法处理
     * @param fileName 文件名
     * @return 返回处理后的文件名
     */
    public static String fileNameSpecialCharacterHandling(String fileName){
        if(fileName.contains("%40")){
            fileName = fileName.replace("%40","@");
        }
        if(fileName.contains("%23")){
            fileName = fileName.replace("%23","#");
        }
        if(fileName.contains("%24")){
            fileName = fileName.replace("%24","$");
        }
        if(fileName.contains("%26")){
            fileName = fileName.replace("%26","&");
        }
        if(fileName.contains("%2B")){
            fileName = fileName.replace("%2B","+");
        }
        if(fileName.contains("%3D")){
            fileName = fileName.replace("%3D","=");
        }
        if(fileName.contains("%3B")){
            fileName = fileName.replace("%3B",";");
        }
        return fileName;
    }
}
