/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */
package codedriver.framework.util.word;

import codedriver.framework.restful.annotation.OperationType;
import codedriver.framework.restful.constvalue.OperationTypeEnum;
import codedriver.framework.restful.core.privateapi.PrivateBinaryStreamApiComponentBase;
import codedriver.framework.util.FileUtil;
import codedriver.framework.util.word.enums.FontColor;
import codedriver.framework.util.word.enums.FontFamily;
import codedriver.framework.util.word.enums.ParagraphAlignmentType;
import codedriver.framework.util.word.enums.TitleType;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author longrf
 * @date 2022/9/23 16:34
 */
@Service
@OperationType(type = OperationTypeEnum.SEARCH)
public class test  extends PrivateBinaryStreamApiComponentBase {

    @Override
    public String getName() {
        return "testWord";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Override
    public Object myDoService(JSONObject paramObj, HttpServletRequest request, HttpServletResponse response) throws Exception {


        String fileName = FileUtil.getEncodedFileName(request.getHeader("User-Agent"), "222" + ".docx");
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", " attachment; filename=\"" + fileName + "\"");

        OutputStream os = response.getOutputStream();


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
            wordBuilder.addBlankRow().addParagraph().setText("测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试")
                    .setCustom(false, FontColor.RED.getValue(), FontFamily.BLACK.getValue(), 12, 2, true, ParagraphAlignmentType.CENTER);
            wordBuilder.addBlankRow();

            wordBuilder.addTable();


//            wordBuilder.addTable().addTableHeaderMap(tableHeaderMap).addBlankRow().addRow(tableValueMap).addRow(tableValueMap);


//            wordBuilder.addParagraph().setContext("测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试")
//                    .setCustom(false, FontColor.BLUE.getValue(), FontFamily.SONG.getValue(), 12, 25, false, ParagraphAlignmentType.RIGHT);
            wordBuilder.addBlankRow();
//            wordBuilder.addBlankRow();

            wordBuilder.addParagraph().setText("1213121312131213121312131213121312131213121312131213121312131213121312131213121312131213121312131213121312131213121312131213121312131213121312131213121312131213");

//            wordBuilder.builder().write(stream);
            wordBuilder.builder().write(os);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getToken() {
        return "word/test";
    }
}
