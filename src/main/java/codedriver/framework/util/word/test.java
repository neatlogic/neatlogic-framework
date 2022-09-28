/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */
package codedriver.framework.util.word;

import codedriver.framework.util.word.enums.FontColor;
import codedriver.framework.util.word.enums.FontFamily;
import codedriver.framework.util.word.enums.ParagraphAlignmentType;
import codedriver.framework.util.word.enums.TitleType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author longrf
 * @date 2022/9/23 16:34
 */

public class test {


    public static void main(String[] args) {

//        wordBuilder.addTable();

        try {
            int i = 1;
            WordBuilder wordBuilder = new WordBuilder();
            //生成新的word
            File file = new File("/Users/longrf/Desktop/003.docx");
            FileOutputStream stream = new FileOutputStream(file);

            Map<Integer, String> tableHeaderMap = new HashMap<>();
            tableHeaderMap.put(1, "ip");
            tableHeaderMap.put(2, "port");
            tableHeaderMap.put(3, "port2");
            tableHeaderMap.put(4, "port44");
            tableHeaderMap.put(5, "port5");
            Map<String, String> tableValueMap = new HashMap<>();
//            tableValueMap.put("port", "22");
            tableValueMap.put("port", "2222222222222222222222222222222222222222");
            tableValueMap.put("ip", "192192192192192192192192192192192192192192");
//            tableValueMap.put("ip", "192");

            wordBuilder.addTitle(TitleType.TILE, "测试").addTitle(TitleType.H1, "测试").addTitle(TitleType.H2, "测试").addTitle(TitleType.H3, "测试");
            wordBuilder.addBlankRow().addParagraph().setContext("测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试")
                    .setCustom(false, FontColor.RED.getValue(), FontFamily.BLACK.getValue(), 12, 2, true, ParagraphAlignmentType.CENTER);
            wordBuilder.addBlankRow();

            wordBuilder.addTable().addTableHeaderMap(tableHeaderMap).addBlankRow().addRow(tableValueMap).addRow(tableValueMap);


//            wordBuilder.addParagraph().setContext("测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试")
//                    .setCustom(false, FontColor.BLUE.getValue(), FontFamily.SONG.getValue(), 12, 25, false, ParagraphAlignmentType.RIGHT);
            wordBuilder.addBlankRow();
//            wordBuilder.addBlankRow();

            wordBuilder.addParagraph().setContext("1213121312131213121312131213121312131213121312131213121312131213121312131213121312131213121312131213121312131213121312131213121312131213121312131213121312131213");

            wordBuilder.builder().write(stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
