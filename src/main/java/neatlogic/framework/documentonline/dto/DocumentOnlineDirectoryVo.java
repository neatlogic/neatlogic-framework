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

package neatlogic.framework.documentonline.dto;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class DocumentOnlineDirectoryVo implements Serializable {

    private static final long serialVersionUID = -928973333311839787L;

    @EntityField(name = "common.name", type = ApiParamType.STRING)
    private final String name;
    @EntityField(name = "common.isfile", type = ApiParamType.BOOLEAN)
    private final boolean isFile;
    @EntityField(name = "common.filepath", type = ApiParamType.STRING)
    private String filePath;
    @EntityField(name = "common.upwardnamelist", type = ApiParamType.STRING)
    private final List<String> upwardNameList = new ArrayList<>();
    @EntityField(name = "common.children", type = ApiParamType.JSONARRAY)
    private List<DocumentOnlineDirectoryVo> children = new ArrayList<>();
    @EntityField(name = "common.configlist", type = ApiParamType.JSONARRAY)
    private final List<DocumentOnlineConfigVo> configList = new ArrayList<>();
    @EntityField(name = "common.prefix", type = ApiParamType.STRING)
    private Integer prefix;
    private boolean allowAddChild = true;

    public DocumentOnlineDirectoryVo(String name, boolean isFile) {
        this.name = name;
        this.isFile = isFile;
    }

    public DocumentOnlineDirectoryVo(Integer prefix, String name, boolean isFile, List<String> upwardNameList) {
        this.prefix = prefix;
        this.name = name;
        this.isFile = isFile;
        this.upwardNameList.addAll(upwardNameList);
    }

    public DocumentOnlineDirectoryVo(Integer prefix, String name, boolean isFile, List<String> upwardNameList, String filePath, List<DocumentOnlineConfigVo> configList) {
        this.prefix = prefix;
        this.name = name;
        this.isFile = isFile;
        this.upwardNameList.addAll(upwardNameList);
        this.filePath = filePath;
        this.configList.addAll(configList);
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
        return new ArrayList<>(children);
    }

    public Integer getPrefix() {
        return prefix;
    }

    public boolean addChild(DocumentOnlineDirectoryVo child) {
        if (!allowAddChild) {
            return false;
        }
        children.add(child);
        return true;
    }

    public void noAllowedAddChild() {
        this.allowAddChild = false;
        for (DocumentOnlineDirectoryVo child : children) {
            child.noAllowedAddChild();
        }
        children.sort((o1, o2) -> {
            if (o1.getPrefix() != null && o2.getPrefix() != null) {
                return Integer.compare(o1.getPrefix(), o2.getPrefix());
            } else if (o1.getPrefix() != null && o2.getPrefix() == null) {
                return -1;
            } else if (o1.getPrefix() == null && o2.getPrefix() != null) {
                return 1;
            } else {
                return String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());
            }
        });
    }

    public List<DocumentOnlineConfigVo> getConfigList() {
        return configList;
    }

    public boolean belongToOwner(String moduleGroup, String menu, JSONObject returnObj) {
        if (CollectionUtils.isEmpty(configList)) {
            return false;
        }
        String anchorPoint = null;
        boolean result = false;
        for (DocumentOnlineConfigVo config : configList) {
            if (!Objects.equals(config.getModuleGroup(), moduleGroup)) {
                continue;
            }
            if (Objects.equals(config.getMenu(), menu)) {
                anchorPoint = config.getAnchorPoint();
                result = true;
                break;
            } else if (StringUtils.isBlank(config.getMenu()) || StringUtils.isBlank(menu)) {
                continue;
            } else if (wildcardMatches(config.getMenu(), menu)) {
                anchorPoint = config.getAnchorPoint();
                result = true;
                break;
            }
        }
        if (returnObj != null) {
            returnObj.put("anchorPoint", anchorPoint);
        }
        return result;
//        if (CollectionUtils.isEmpty(ownerList)) {
//            return false;
//        }
//        for (String owner : ownerList) {
//            String[] keyAndValueArray = owner.split("&");
//            for (String keyAndValue : keyAndValueArray) {
//                String[] split = keyAndValue.split("=");
//                if (Objects.equals(split[0], "moduleGroup")) {
//                    if (!Objects.equals(split[1], moduleGroup)) {
//                        return false;
//                    }
//                } else if (Objects.equals(split[0], "menu")) {
//                    if (StringUtils.isNotBlank(menu)) {
//                        if (!Objects.equals(split[1], menu)) {
//                            if (!wildcardMatches(split[1], menu)) {
//                                return false;
//                            }
//                        }
//                    }
//                }  else if (Objects.equals(split[0], "anchorPoint")) {
//                    if (returnObj != null) {
//                        returnObj.put("anchorPoint", split[1]);
//                    }
//                }
//            }
//        }
//        return true;
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
                    stringBuilder.append(".*");
                } else if (Objects.equals(wildcardCharacter, "?")) {
                    stringBuilder.append(".{1}");
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
                stringBuilder.append(".*");
            } else if (Objects.equals(wildcardCharacter, "?")) {
                stringBuilder.append(".{1}");
            }
        }
        String regex = stringBuilder.toString();
        // 通过正则表达式判断是否匹配
        return Pattern.matches(regex, menu);
    }
}
