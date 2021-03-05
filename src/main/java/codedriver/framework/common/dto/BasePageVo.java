package codedriver.framework.common.dto;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.fulltextindex.utils.FullTextIndexUtil;
import codedriver.framework.restful.annotation.EntityField;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Set;

public class BasePageVo {
    @JSONField(serialize = false)
    private transient Boolean needPage = true;
    @JSONField(serialize = false)
    @EntityField(name = "每页条数", type = ApiParamType.INTEGER)
    private transient Integer pageSize = 20;
    @JSONField(serialize = false)
    @EntityField(name = "当前页数", type = ApiParamType.INTEGER)
    private transient Integer currentPage = 1;
    @JSONField(serialize = false)
    @EntityField(name = "页数", type = ApiParamType.INTEGER)
    private transient Integer pageCount = 0;
    @JSONField(serialize = false)
    private transient Integer startNum;
    @JSONField(serialize = false)
    private transient String keyword;
    @JSONField(serialize = false)
    private transient Set<String> keywordList;
    @EntityField(name = "总条数", type = ApiParamType.INTEGER)
    private transient Integer rowNum = 0;

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
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        if (pageSize != null) {
            this.pageSize = Math.min(100, pageSize);//pagesize最大100
        }
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
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public String getKeyword() {
        return keyword;
    }

    public Set<String> getKeywordList() {
        if (CollectionUtils.isEmpty(this.keywordList) && StringUtils.isNotBlank(keyword)) {
            try {
                this.keywordList = FullTextIndexUtil.sliceKeyword(keyword);
            } catch (IOException ignored) {
            }
        }
        return this.keywordList;
    }


    public void setKeyword(String keyword) {
        if (StringUtils.isNotBlank(keyword)) {
            this.keyword = keyword.trim();
        }
    }
}
