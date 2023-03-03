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

package neatlogic.framework.fulltextindex.dto.globalsearch;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.fulltextindex.dto.fulltextindex.FullTextIndexWordVo;
import neatlogic.framework.fulltextindex.utils.FullTextIndexUtil;
import neatlogic.framework.restful.annotation.EntityField;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DocumentVo extends BasePageVo {
    private final Logger logger = LoggerFactory.getLogger(DocumentVo.class);
    @EntityField(name = "目标id", type = ApiParamType.LONG)
    private Long targetId;
    @EntityField(name = "标题", type = ApiParamType.STRING)
    private String title;
    @EntityField(name = "内容", type = ApiParamType.STRING)
    private String content;
    @EntityField(name = "高亮内容", type = ApiParamType.STRING)
    private String highlightContent;
    @EntityField(name = "类型", type = ApiParamType.STRING)
    private String type;
    @EntityField(name = "类型名称", type = ApiParamType.STRING)
    private String typeText;
    @EntityField(name = "目标链接", type = ApiParamType.STRING)
    private String targetUrl;
    @JSONField(serialize = false)//查询条件：类型列表
    private List<String> typeList;
    @JSONField(serialize = false)//查询条件：文档所属模块
    private String moduleId;
    @JSONField(serialize = false)//查询条件：分词列表
    private List<FullTextIndexWordVo> fullTextIndexWordList;
    @EntityField(name = "关键字分词结果", type = ApiParamType.JSONARRAY)
    private Set<String> wordList;
    @EntityField(name = "是否有权限查看", type = ApiParamType.BOOLEAN)
    private Boolean canRead = true;
    @EntityField(name = "搜索命中次数", type = ApiParamType.INTEGER)
    private int hitCount = 0;
    @EntityField(name = "用户点击次数", type = ApiParamType.INTEGER)
    private int clickCount = 0;

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHighlightContent() {
        return highlightContent;
    }

    public void setHighlightContent(String highlightContent) {
        this.highlightContent = highlightContent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        if (this.typeList == null) {
            this.typeList = new ArrayList<>();
        }
        if (!this.typeList.contains(type)) {
            this.typeList.add(type);
        }
    }

    public String getTypeText() {
        return typeText;
    }

    public void setTypeText(String typeText) {
        this.typeText = typeText;
    }

    public int getHitCount() {
        return hitCount;
    }

    public void setHitCount(int hitCount) {
        this.hitCount = hitCount;
    }

    public int getClickCount() {
        return clickCount;
    }

    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }

    public Set<String> getWordList() {
        if (CollectionUtils.isEmpty(wordList) && StringUtils.isNotBlank(getKeyword())) {
            try {
                wordList = FullTextIndexUtil.sliceKeyword(getKeyword());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        return wordList;
    }


    public List<String> getTypeList() {
        return typeList;
    }

    public void setTypeList(List<String> typeList) {
        this.typeList = typeList;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public List<FullTextIndexWordVo> getFullTextIndexWordList() {
        return fullTextIndexWordList;
    }

    public void setFullTextIndexWordList(List<FullTextIndexWordVo> fullTextIndexWordList) {
        this.fullTextIndexWordList = fullTextIndexWordList;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public Boolean getCanRead() {
        return canRead;
    }

    public void setCanRead(Boolean canRead) {
        this.canRead = canRead;
    }
}
