/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.common.dto;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.util.PageUtil;
import codedriver.framework.fulltextindex.utils.FullTextIndexUtil;
import codedriver.framework.restful.annotation.EntityField;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.Set;

public class BasePageVo implements Serializable {
    @JSONField(serialize = false)
    private boolean needPage = true;
    @JSONField(serialize = false)
    @EntityField(name = "每页条数", type = ApiParamType.INTEGER)
    private Integer pageSize = 20;
    @JSONField(serialize = false)
    @EntityField(name = "当前页数", type = ApiParamType.INTEGER)
    private Integer currentPage = 1;
    @JSONField(serialize = false)
    @EntityField(name = "页数", type = ApiParamType.INTEGER)
    private Integer pageCount = 0;
    @JSONField(serialize = false)
    private Integer startNum;
    @JSONField(serialize = false)
    private String keyword;
    @JSONField(serialize = false)
    private Set<String> keywordList;
    @EntityField(name = "总条数", type = ApiParamType.INTEGER)
    @JSONField(serialize = false)
    private Integer rowNum = 0;
    @JSONField(serialize = false)
    private Integer pageSizePlus;
    @EntityField(name = "默认值", type = ApiParamType.JSONARRAY)
    @JSONField(serialize = false)
    private JSONArray defaultValue;
    @JSONField(serialize = false)
    private Long cacheFlushKey;//用于扰乱mybatis的Level 1 Cache

    public BasePageVo() {
    }

    public Integer getRowNum() {
        return rowNum;
    }

    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }

    public Boolean getNeedPage() {
        return true;
    }

    public void setNeedPage(Boolean needPage) {
        this.needPage = needPage;
    }

    public Integer getPageSize() {
        if (!this.needPage) {
            pageSize = 100;
        }
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        if (pageSize != null && pageSize > 0) {
            this.pageSize = Math.min(100, pageSize);//pagesize最大100
        }
    }

    public Integer getPageSizePlus() {
        return this.getPageSize() + 1;
    }


    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        if (currentPage == null) {
            this.currentPage = 1;
        } else {
            this.currentPage = currentPage;
        }
    }

    public Integer getStartNum() {
        return Math.max((getCurrentPage() - 1) * getPageSize(), 0);
    }

    public void setStartNum(Integer startNum) {
        this.startNum = startNum;
    }

    public Integer getPageCount() {
        if ((pageCount == null || pageCount == 0) && rowNum != null && rowNum > 0) {
            pageCount = PageUtil.getPageCount(rowNum, pageSize);
        }
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public final String getKeyword() {
        return keyword;
    }

    public final Set<String> getKeywordList() {
        if (CollectionUtils.isEmpty(this.keywordList) && StringUtils.isNotBlank(keyword)) {
            try {
                this.keywordList = FullTextIndexUtil.sliceKeyword(keyword);
            } catch (IOException ignored) {
            }
        }
        return this.keywordList;
    }


    public final void setKeyword(String keyword) {
        if (StringUtils.isNotBlank(keyword)) {
            this.keyword = keyword.trim();
        }
    }

    public JSONArray getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(JSONArray defaultValue) {
        this.defaultValue = defaultValue;
    }

    public final Long getCacheFlushKey() {
        return cacheFlushKey;
    }

    public final void setCacheFlushKey(Long cacheFlushKey) {
        this.cacheFlushKey = cacheFlushKey;
    }
}
