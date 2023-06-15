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

package neatlogic.framework.documentonline.dto;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class DocumentOnlineDirectoryVo {

    private final String name;
    private final boolean isFile;
    private String filePath;
    private final List<String> upwardNameList = new ArrayList<>();
    private final List<String> ownerList = new ArrayList<>();
    private List<DocumentOnlineDirectoryVo> children = new ArrayList<>();

    public DocumentOnlineDirectoryVo(String name, boolean isFile) {
        this.name = name;
        this.isFile = isFile;
    }

    public DocumentOnlineDirectoryVo(String name, boolean isFile, List<String> upwardNameList) {
        this.name = name;
        this.isFile = isFile;
        this.upwardNameList.addAll(upwardNameList);
    }

    public DocumentOnlineDirectoryVo(String name, boolean isFile, List<String> upwardNameList, String filePath, List<String> ownerList) {
        this.name = name;
        this.isFile = isFile;
        this.upwardNameList.addAll(upwardNameList);
        this.filePath = filePath;
        this.ownerList.addAll(ownerList);
    }

    public String getName() {
        return name;
    }

    public boolean getIsFile() {
        return isFile;
    }

    public List<String> getUpwardNameList() {
        return new ArrayList<>(upwardNameList);
    }

    public String getFilePath() {
        return filePath;
    }

    public List<DocumentOnlineDirectoryVo> getChildren() {
        return children;
    }

    public void addChild(DocumentOnlineDirectoryVo child) {
        children.add(child);
    }

    public boolean belongToOwner(String moduleGroup, String menu, JSONObject returnObj) {
        if (CollectionUtils.isEmpty(ownerList)) {
            return false;
        }
        for (String owner : ownerList) {
            String[] keyAndValueArray = owner.split("&");
            for (String keyAndValue : keyAndValueArray) {
                String[] split = keyAndValue.split("=");
                if (Objects.equals(split[0], "moduleGroup")) {
                    if (!Objects.equals(split[1], moduleGroup)) {
                        return false;
                    }
                } else if (Objects.equals(split[0], "menu")) {
                    if (StringUtils.isNotBlank(menu)) {
                        if (!Objects.equals(split[1], menu)) {
                            if (!wildcardMatches(split[1], menu)) {
                                return false;
                            }
                        }
                    }
                }  else if (Objects.equals(split[0], "anchorPoint")) {
                    if (returnObj != null) {
                        returnObj.put("anchorPoint", split[1]);
                    }
                }
            }
        }
        return true;
    }
    /**
     * 通配符匹配，*代表任意个数的字符，?代表有且仅有一个字符。
     * @param wildcardPattern 通配符模板
     * @param menu 菜单路由
     * @return 返回匹配结果
     */
    private boolean wildcardMatches(String wildcardPattern, String menu) {
        // 先通过*和?对通配符模板进行分隔，得到必须相同的字符串列表
        List<String> sameWordList = new ArrayList<>();
        String[] split = wildcardPattern.split("\\*|\\?");
        for (String str : split) {
            if (StringUtils.isNotBlank(str)) {
                sameWordList.add(str);
            }
        }
        // 遍历必须相同的字符串列表，找到它们在通配符模板中的位置下标，组装出对应的正则表达式
        StringBuilder stringBuilder = new StringBuilder();
        int beginIndex = 0;
        for (String sameWord : sameWordList) {
            int index = wildcardPattern.indexOf(sameWord);
            if (beginIndex < index) {
                String wildcardCharacter = wildcardPattern.substring(beginIndex, index);
                if (Objects.equals(wildcardCharacter, "*")) {
                    stringBuilder.append("[\\u4E00-\\u9FA5_\\w]*");
                } else if (Objects.equals(wildcardCharacter, "?")) {
                    stringBuilder.append("[\\u4E00-\\u9FA5_\\w]{1}");
                }
            }
            stringBuilder.append("(");
            stringBuilder.append(sameWord);
            stringBuilder.append("){1}");
            beginIndex = index + sameWord.length();
        }
        if (beginIndex < wildcardPattern.length()) {
            String wildcardCharacter = wildcardPattern.substring(beginIndex);
            if (Objects.equals(wildcardCharacter, "*")) {
                stringBuilder.append("[\\u4E00-\\u9FA5_\\w]*");
            } else if (Objects.equals(wildcardCharacter, "?")) {
                stringBuilder.append("[\\u4E00-\\u9FA5_\\w]{1}");
            }
        }
        String regex = stringBuilder.toString();
        // 通过正则表达式判断是否匹配
        return Pattern.matches(regex, menu);
    }
}
