/*
 * Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.store.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.dto.ElasticsearchVo;
import neatlogic.framework.dto.elasticsearch.IndexResultVo;
import neatlogic.framework.exception.elasticsearch.ElasticSearchCreateDocumentException;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ElasticsearchIndexBase<T> implements IElasticsearchIndex<T> {
    static Logger logger = LoggerFactory.getLogger(ElasticsearchIndexBase.class);

    public final String getIndexName() {
        return (TenantContext.get().getTenantUuid() + "_" + this.getName()).toLowerCase();
    }


    // 创建索引
    public final void createIndex() {
        if (!this.isIndexExists() && ElasticsearchClientFactory.getClient() != null) {
            ElasticsearchVo elasticsearchVo = ElasticsearchClientFactory.getElasticsearchVo();
            if (elasticsearchVo != null) {
                this.myCreateIndex(elasticsearchVo);
            }
        }
    }

    protected abstract void myCreateIndex(ElasticsearchVo elasticsearchVo);

    // 判断索引是否存在
    protected boolean isIndexExists() {
        try {
            ElasticsearchClient client = ElasticsearchClientFactory.getClient();
            GetIndexResponse response = client.indices().get(g -> g.index(this.getIndexName()));
            return response.result().containsKey(this.getIndexName());
        } catch (Exception e) {
            // 索引不存在会抛异常
            return false;
        }
    }

    protected abstract void myCreateDocument(T targetVo) throws Exception;

    @Override
    public final void createDocument(T targetVo) {
        try {
            this.myCreateDocument(targetVo);
        } catch (Exception e) {
            throw new ElasticSearchCreateDocumentException(e);
        }
    }

    private static Query buildQuery(Map<String, Object> conditionObj) {
        // 如果条件为空，使用 match_all
        if (MapUtils.isEmpty(conditionObj)) {
            return new Query.Builder()
                    .matchAll(ma -> ma)
                    .build();
        }

        // 构建查询条件列表
        List<Query> queries = new ArrayList<>();
        conditionObj.forEach((key, value) -> {
            if (value != null && StringUtils.isNotBlank(value.toString())) { // 跳过空值
                queries.add(new Query.Builder()
                        .match(ma -> ma.field(key).query(FieldValue.of(value)))
                        .build());
            }
        });

        // 构建 bool 查询
        return new Query.Builder()
                .bool(b -> b.must(queries))
                .build();
    }

    @Override
    public final IndexResultVo searchDocument(Map<String, Object> conditionObj, Integer currentPage, Integer pageSize) throws IOException {

        // 构建查询
        Query queryBuilder = buildQuery(conditionObj);

        // 执行搜索
        ElasticsearchClient client = ElasticsearchClientFactory.getClient();

        // 创建搜索请求总数
        SearchRequest requestCount = new SearchRequest.Builder()
                .index(this.getIndexName())
                .query(queryBuilder) // 搜索条件
                .size(0)      // 设置 size 为 0，仅获取总量
                .build();

        // 执行搜索
        SearchResponse<Object> responseCount = client.search(requestCount, Object.class);

        // 获取总量
        long rowNum = responseCount.hits().total().value();
        IndexResultVo resultVo = new IndexResultVo();
        resultVo.setCurrentPage(currentPage);
        resultVo.setPageSize(pageSize);
        resultVo.setRowNum((int) rowNum);
        // 创建搜索请求
        SearchRequest request = new SearchRequest.Builder()
                .index(this.getIndexName())
                .query(queryBuilder)
                .from(resultVo.getStartNum())
                .size(resultVo.getPageSize())
                .build();


        SearchResponse<Object> response = client.search(request, Object.class);

        // 提取符合条件的 id 列表
        List<String> idList = new ArrayList<>();
        List<Hit<Object>> hits = response.hits().hits();
        for (Hit<Object> hit : hits) {
            idList.add(hit.id());
        }
        resultVo.setIdList(idList);
        return resultVo;
    }
}
