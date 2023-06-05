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

package neatlogic.framework.common.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.util.PageUtil;
import neatlogic.framework.fulltextindex.utils.FullTextIndexUtil;
import neatlogic.framework.restful.annotation.EntityField;
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
    @EntityField(name = "common.pagesize", type = ApiParamType.INTEGER)
    private Integer pageSize = 20;
    @JSONField(serialize = false)
    @EntityField(name = "common.currentpage", type = ApiParamType.INTEGER)
    private Integer currentPage = 1;
    @JSONField(serialize = false)
    @EntityField(name = "common.pagecount", type = ApiParamType.INTEGER)
    private Integer pageCount = 0;

    @JSONField(serialize = false)//数据页范围，用于查询一定范围内的数据
    private Integer[] pageRange;
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

    public Integer[] getPageRange() {
        return pageRange;
    }

    public void setPageRange(Integer[] pageRange) {
        if (pageRange != null && pageRange.length == 2 && pageRange[0] >= 1 && pageRange[1] >= 1 && pageRange[1] >= pageRange[0]) {
            this.pageRange = pageRange;
        }
    }

    public Integer getCurrentPage() {
        if (pageRange == null) {
            return currentPage;
        } else {
            return pageRange[0];
        }
    }

    public Integer getPageSize() {
        if (pageRange == null) {
            if (!this.needPage) {
                pageSize = 100;
            }
            return pageSize;
        } else {
            return pageSize * (pageRange[1] - pageRange[0] + 1);
        }
    }

    public void setPageSize(Integer pageSize) {
        if (pageSize != null && pageSize > 0) {
            this.pageSize = Math.min(100, pageSize);//pagesize最大100
        }
    }

    public Integer getPageSizePlus() {
        return this.getPageSize() + 1;
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
