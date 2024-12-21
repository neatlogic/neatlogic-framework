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
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.CountRequest;
import co.elastic.clients.elasticsearch.core.CountResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.asynchronization.threadpool.CachedThreadPool;
import neatlogic.framework.dto.ElasticsearchVo;
import neatlogic.framework.dto.elasticsearch.IndexResultVo;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.exception.elasticsearch.ElasticSearchDeleteIndexException;
import neatlogic.framework.fulltextindex.dao.mapper.FullTextIndexRebuildAuditMapper;
import neatlogic.framework.fulltextindex.dto.fulltextindex.FullTextIndexRebuildAuditVo;
import neatlogic.framework.fulltextindex.enums.FullTextIndexHandlerType;
import neatlogic.framework.fulltextindex.enums.Status;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public abstract class ElasticsearchIndexBase<T> implements IElasticsearchIndex<T> {
    static Logger logger = LoggerFactory.getLogger(ElasticsearchIndexBase.class);
    @Resource
    private FullTextIndexRebuildAuditMapper fullTextIndexRebuildAuditMapper;

    public final String getIndexName() {
        return (TenantContext.get().getTenantUuid() + "_" + this.getName()).toLowerCase();
    }

    @Override
    public final T getDocument(T target) {
        return myGetDocument(target);
    }

    protected abstract T myGetDocument(T target);

    @Override
    public int getDocumentCount() {
        ElasticsearchClient client = ElasticsearchClientFactory.getClient();
        CountRequest countRequest = new CountRequest.Builder()
                .index(getIndexName()) // 指定索引名称
                .query(q -> q.matchAll(m -> m)) // 使用 MatchAll 查询来匹配所有文档
                .build();
        CountResponse countResponse = null;
        try {
            countResponse = client.count(countRequest);
            long documentCount = countResponse.count();
            return (int) documentCount;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return 0;
        }
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

    public final void deleteIndex() {
        // 删除索引
        ElasticsearchClient client = ElasticsearchClientFactory.getClient();
        if (this.isIndexExists() && client != null) {
            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest.Builder()
                    .index(this.getIndexName()) // 索引名称
                    .build();
            try {
                client.indices().delete(deleteIndexRequest);
            } catch (Exception e) {
                throw new ElasticSearchDeleteIndexException(e);
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

    @Override
    public final void rebuildDocument(boolean isAll) {
        FullTextIndexRebuildAuditVo auditVo = new FullTextIndexRebuildAuditVo();
        auditVo.setType(this.getName());
        auditVo.setEditor(UserContext.get().getUserUuid(true));
        auditVo.setStatus(Status.DOING.getValue());
        auditVo.setHandler(FullTextIndexHandlerType.ELASTICSEARCH.getValue());
        fullTextIndexRebuildAuditMapper.insertFullTextIndexRebuildAudit(auditVo);
        if (isAll) {
            this.deleteIndex();
            this.createIndex();
        }
        CachedThreadPool.execute(new NeatLogicThread("ELASTICSEARCH-INDEX-REBUILD-" + this.getIndexName()) {
            @Override
            protected void execute() {
                try {
                    myRebuildDocument(isAll);
                } catch (ApiRuntimeException ex) {
                    auditVo.setError(ex.getMessage());
                } catch (Exception ex) {
                    auditVo.setError(ExceptionUtils.getStackTrace(ex));
                }
                auditVo.setStatus(Status.DONE.getValue());
                fullTextIndexRebuildAuditMapper.updateFullTextIndexRebuildAuditStatus(auditVo);
            }
        });
    }

    protected abstract void myRebuildDocument(boolean isAll);


    protected abstract void myCreateDocument(T targetVo);

    @Override
    public final void createDocument(T targetVo) {
        this.myCreateDocument(targetVo);
    }

    @Override
    public final void deleteDocument(T targetVo) {
        this.myDeleteDocument(targetVo);
    }


    protected abstract void myDeleteDocument(T targetVo);

    /*private static Query buildQuery(Map<String, Object> conditionObj) {
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
    }*/

    @Override
    public final IndexResultVo searchDocument(T targetVo, Integer currentPage, Integer pageSize) {

        // 构建查询
        Query queryBuilder = this.buildQuery(targetVo);

        // 执行搜索
        ElasticsearchClient client = ElasticsearchClientFactory.getClient();

        // 创建搜索请求总数
        SearchRequest requestCount = new SearchRequest.Builder()
                .index(this.getIndexName())
                .query(queryBuilder) // 搜索条件
                .size(0)      // 设置 size 为 0，仅获取总量
                .build();

        IndexResultVo resultVo = new IndexResultVo();
        if (this.needPage(targetVo)) {
            resultVo.setCurrentPage(currentPage);
            resultVo.setPageSize(pageSize);
        }
        try {
            // 执行搜索
            SearchResponse<Object> responseCount = client.search(requestCount, Object.class);

            // 获取总量
            long rowNum = responseCount.hits().total().value();


            resultVo.setRowNum((int) rowNum);
            // 创建搜索请求
            SearchRequest.Builder builder = new SearchRequest.Builder()
                    .index(this.getIndexName())
                    .query(queryBuilder);
            if (this.needPage(targetVo)) {
                builder.from(resultVo.getStartNum())
                        .size(resultVo.getPageSize());
            } else {
                builder.size(99);
            }

            SearchRequest request = builder.build();


            SearchResponse<Object> response = client.search(request, Object.class);

            // 提取符合条件的 id 列表
            List<String> idList = new ArrayList<>();
            List<Hit<Object>> hits = response.hits().hits();
            for (Hit<Object> hit : hits) {
                idList.add(hit.id());
            }
            resultVo.setIdList(idList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return resultVo;
    }
}
