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

package neatlogic.framework.fulltextindex.dto.globalsearch;

import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.fulltextindex.dto.fulltextindex.FullTextIndexWordVo;
import neatlogic.framework.fulltextindex.utils.FullTextIndexUtil;
import neatlogic.framework.restful.annotation.EntityField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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
    @JSONField(serialize = false)
    private Integer needOffset;//是否需要返回关键字的位置
    @JSONField(serialize = false)//查询条件：类型列表
    private List<String> typeList;
    @JSONField(serialize = false)//查询条件：文档所属模块
    private String moduleId;
    @JSONField(serialize = false)//查询条件：分词列表
    private List<FullTextIndexWordVo> fullTextIndexWordList;
    @EntityField(name = "关键字分词结果", type = ApiParamType.JSONARRAY)
    private List<String> wordList;
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

    public Integer getNeedOffset() {
        return needOffset;
    }

    public void setNeedOffset(Integer needOffset) {
        this.needOffset = needOffset;
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

    public List<String> getWordList() {
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
