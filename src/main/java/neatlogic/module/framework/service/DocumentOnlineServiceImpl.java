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

package neatlogic.module.framework.service;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.documentonline.crossover.DocumentOnlineServiceCrossoverService;
import neatlogic.framework.documentonline.dto.DocumentOnlineDirectoryVo;
import neatlogic.framework.documentonline.dto.DocumentOnlineVo;
import neatlogic.module.framework.startup.DocumentOnlineInitializeIndexHandler;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.text.TextContentRenderer;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

@Service
public class DocumentOnlineServiceImpl implements DocumentOnlineService, DocumentOnlineServiceCrossoverService {

    @Override
    public String interceptsSpecifiedNumberOfCharacters(InputStream inputStream, int skip, int number) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            bufferedReader.skip(skip);
            while (stringBuilder.length() < number) {
                String lineContent = bufferedReader.readLine();
                if (lineContent == null) {
                    break;
                }
                if (StringUtils.isBlank(lineContent)) {
                    continue;
                }

                lineContent = removeImagePath(lineContent);
                if (StringUtils.isBlank(lineContent)) {
                    continue;
                }
                Parser parser = Parser.builder().build();
                Node document = parser.parse(lineContent);
                TextContentRenderer textContentRenderer = TextContentRenderer.builder().build();
                lineContent = textContentRenderer.render(document);
                if (StringUtils.isBlank(lineContent)) {
                    continue;
                }
                if (lineContent.length() > number - stringBuilder.length()) {
                    stringBuilder.append(lineContent.substring(0, number - stringBuilder.length()));
                } else {
                    stringBuilder.append(lineContent);
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 将文档内容中图片删除
     * @param content 文档内容
     * @return
     */
    private String removeImagePath(String content) {
        StringBuilder stringBuilder = new StringBuilder();
        int beginIndex = 0;
        Matcher figureMatcher = DocumentOnlineInitializeIndexHandler.MARKDOWN_IMAGE_PATTERN.matcher(content);
        while (figureMatcher.find()) {
            String group = figureMatcher.group();
            int index = content.indexOf(group, beginIndex);
            String subStr = content.substring(beginIndex, index);
            stringBuilder.append(subStr);
            beginIndex = index + group.length();
        }
        stringBuilder.append(content.substring(beginIndex));
        return stringBuilder.toString();
    }

    @Override
    public List<DocumentOnlineVo> getAllFileList(DocumentOnlineDirectoryVo directory, String moduleGroup, String menu) {
        List<DocumentOnlineVo> list = new ArrayList<>();
        for (DocumentOnlineDirectoryVo child : directory.getChildren()) {
            if (child.getIsFile()) {
                JSONObject returnObj = new JSONObject();
                if (StringUtils.isBlank(moduleGroup) || child.belongToOwner(moduleGroup, menu, returnObj)) {
                    DocumentOnlineVo documentOnlineVo = new DocumentOnlineVo();
                    documentOnlineVo.setUpwardNameList(child.getUpwardNameList());
                    documentOnlineVo.setFileName(child.getName());
                    documentOnlineVo.setFilePath(child.getFilePath());
                    String anchorPoint = returnObj.getString("anchorPoint");
                    documentOnlineVo.setAnchorPoint(anchorPoint);
                    list.add(documentOnlineVo);
                }
            } else {
                list.addAll(getAllFileList(child, moduleGroup, menu));
            }
        }
        return list;
    }

    @Override
    public List<DocumentOnlineVo> getAllFileList(DocumentOnlineDirectoryVo directory) {
        return getAllFileList(directory, null, null);
    }
}
