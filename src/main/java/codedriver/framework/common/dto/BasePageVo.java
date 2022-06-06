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

import java.io.Serializable;
import java.util.Set;

public class BasePageVo implements Serializable {
    //以当前页起最多展示页码数
    private static final int OFFSET_SIZE = 5;
    private static final long serialVersionUID = 6550790567158160404L;
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
    @EntityField(name = "开始页数", type = ApiParamType.INTEGER)
    private Integer startPage;
    @EntityField(name = "结束页数", type = ApiParamType.INTEGER)
    private Integer endPage;
    @EntityField(name = "以当前页起实际分页条数", type = ApiParamType.INTEGER)
    @JSONField(serialize = false)
    private Integer offsetRowNum;
    @EntityField(name = "以当前页起预计分页条数", type = ApiParamType.INTEGER)
    @JSONField(serialize = false)
    private Integer expectOffsetRowNum;

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
            this.keywordList = FullTextIndexUtil.sliceKeyword(keyword);
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

    public Integer getStartPage() {
        if (currentPage < OFFSET_SIZE + 2) {
            return 1;
        } else {
            return currentPage - OFFSET_SIZE;
        }
    }

    public Integer getEndPage() {
        if (offsetRowNum != null) {
            if (offsetRowNum % pageSize > 0) {
                return currentPage + offsetRowNum / pageSize;
            }
            return currentPage + offsetRowNum / pageSize - 1;
        }
        return null;
    }

    public void setOffsetRowNum(Integer offsetRowNum) {
        this.offsetRowNum = offsetRowNum;
    }

    public Integer getExpectOffsetRowNum() {
        if (expectOffsetRowNum == null) {
            int expectOffsetSize;
            if (currentPage < OFFSET_SIZE + 2) {
                expectOffsetSize = OFFSET_SIZE * 2 - (currentPage - 1);
            } else {
                expectOffsetSize = OFFSET_SIZE;
            }
            return expectOffsetSize * pageSize;
        }
        return expectOffsetRowNum;
    }

    public void setExpectOffsetRowNum(Integer expectOffsetRowNum) {
        this.expectOffsetRowNum = expectOffsetRowNum;
    }
}
