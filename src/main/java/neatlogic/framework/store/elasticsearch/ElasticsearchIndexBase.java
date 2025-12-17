/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.store.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.*;
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
import neatlogic.framework.exception.elasticsearch.ElasticSearchCreateDocumentException;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ElasticsearchIndexBase<T> implements IElasticsearchIndex<T> {
    static Logger logger = LoggerFactory.getLogger(ElasticsearchIndexBase.class);
    private static final ConcurrentHashMap<Long, Object> LOCK_MAP = new ConcurrentHashMap<>();

    private static Object getLock(Long key) {
        // 同 key 共享同一个对象实例
        return LOCK_MAP.computeIfAbsent(key, k -> new Object());
    }

    private static void releaseLock(Long key) {
        Object lock = LOCK_MAP.get(key);
        if (lock != null) {
            // 原子删除，避免新线程在删除过程中刚创建同 key 的锁被误删
            LOCK_MAP.remove(key, lock);
        }
    }


    @Resource
    private FullTextIndexRebuildAuditMapper fullTextIndexRebuildAuditMapper;

    public final String getIndexName() {
        return (TenantContext.get().getTenantUuid() + "_" + this.getName()).toLowerCase();
    }

    @Override
    public final void updateDocument(Long targetId, Map<String, Object> document, boolean isUpsert) {
        if (targetId == null) return;
        Object lock = getLock(targetId);
        // synchronized 保证同一 targetId 串行
        synchronized (lock) {
            try {
                ElasticsearchClient client = ElasticsearchClientFactory.getClient();
                UpdateRequest<Object, Map<String, Object>> updateRequest = new UpdateRequest.Builder<Object, Map<String, Object>>()
                        .index(getIndexName())                   // 索引名称
                        .id(targetId.toString())          // 文档 ID
                        .docAsUpsert(isUpsert)
                        .doc(document)                            // 需要更新的字段
                        .build();
                client.update(updateRequest, Object.class);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        releaseLock(targetId);
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
                    logger.error(ex.getMessage(), ex);
                    auditVo.setError(ex.getMessage());
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                    auditVo.setError(ExceptionUtils.getStackTrace(ex));
                }
                auditVo.setStatus(Status.DONE.getValue());
                fullTextIndexRebuildAuditMapper.updateFullTextIndexRebuildAuditStatus(auditVo);
            }
        });
    }

    //创建自定义排序排序
    protected abstract void mySortQuery(SearchRequest.Builder builder, T targetVo);

    //创建自定义高亮
    protected abstract void myHighlight(SearchRequest.Builder builder);

    //创建自定义查询
    protected abstract Query myBuildQuery(T targetVo);

    protected abstract void myRebuildDocument(boolean isAll);


    protected abstract void myCreateDocument(T targetVo);

    protected abstract void myCreateDocument(Long targetId);


    @Override
    public final void createDocument(T targetVo) {
        this.myCreateDocument(targetVo);
    }

    @Override
    public final void createDocument(Long targetId) {
        this.myCreateDocument(targetId);
    }

    protected final void createDocument(Long id, Map<String, Object> document) {
        ElasticsearchClient client = ElasticsearchClientFactory.getClient();
        // 创建或更新文档
        IndexRequest<Map<String, Object>> request = new IndexRequest.Builder<Map<String, Object>>()
                .index(getIndexName()) // 索引名称
                .id(id.toString())      // 文档 ID
                .document(document) // 文档内容
                .build();

        // 执行请求
        try {
            client.index(request);
        } catch (Exception ex) {
            //logger.error(ex.getMessage(), ex);
            throw new ElasticSearchCreateDocumentException(ex);
        }
    }


    @Override
    public final void deleteDocument(Long targetId) {
        this.myDeleteDocument(targetId);
    }


    protected abstract void myDeleteDocument(Long targetId);


    @Override
    public final long searchDocumentCount(T targetVo) {
        Query queryBuilder = this.myBuildQuery(targetVo);
        ElasticsearchClient client = ElasticsearchClientFactory.getClient();

        try {
            CountResponse resp = client.count(c -> c
                    .index(this.getIndexName())
                    .query(queryBuilder)
            );
            return resp.count();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return 0;
        }
    }

    /*
    @Override
    public final IndexResultVo searchDocument(T targetVo, Integer currentPage, Integer pageSize) {

        // 构建查询
        Query queryBuilder = this.myBuildQuery(targetVo);

        // 执行搜索
        ElasticsearchClient client = ElasticsearchClientFactory.getClient();

        // 创建搜索请求总数
        SearchRequest.Builder requestCountBuilder = new SearchRequest.Builder()
                .index(this.getIndexName())
                .size(0);      // 设置 size 为 0，仅获取总量
        if (queryBuilder != null) {
            requestCountBuilder.query(queryBuilder);
        }

        SearchRequest requestCount = requestCountBuilder.build();

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

            // 添加排序条件
            SearchRequest.Builder builder = new SearchRequest.Builder()
                    .index(this.getIndexName());

            if (queryBuilder != null) {
                builder.query(queryBuilder);
            }

            //高亮
            this.myHighlight(builder);

            //排序
            this.mySortQuery(builder, targetVo);

            if (this.needPage(targetVo)) {
                builder.from(resultVo.getStartNum())
                        .size(resultVo.getPageSize());
            } else {
                builder.size(100);
            }

            SearchRequest request = builder.build();


            SearchResponse<Object> response = client.search(request, Object.class);

            // 提取符合条件的 id 列表
            List<String> idList = new ArrayList<>();
            List<IndexResultHighlightVo> highlightList = new ArrayList<>();
            List<Hit<Object>> hits = response.hits().hits();
            for (Hit<Object> hit : hits) {
                idList.add(hit.id());
                Map<String, List<String>> hm = hit.highlight();
                if (MapUtils.isNotEmpty(hm)) {
                    IndexResultHighlightVo highlightVo = new IndexResultHighlightVo();
                    highlightVo.setId(hit.id());
                    highlightVo.setHighlightMap(hm);
                    highlightList.add(highlightVo);
                }
            }
            resultVo.setHighlightList(highlightList);
            resultVo.setIdList(idList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return resultVo;
    }*/

    @Override
    public final IndexResultVo searchDocument(T targetVo, Integer currentPage, Integer pageSize) {

        // 构建查询
        Query queryBuilder = this.myBuildQuery(targetVo);

        // 执行搜索
        ElasticsearchClient client = ElasticsearchClientFactory.getClient();


        IndexResultVo resultVo = new IndexResultVo();
        if (this.needPage(targetVo)) {
            resultVo.setCurrentPage(currentPage);
            resultVo.setPageSize(pageSize);
        }
        try {
            SearchRequest.Builder builder = new SearchRequest.Builder()
                    .index(this.getIndexName())
                    .source(s -> s.fetch(false));//不返回 _source
            //.trackTotalHits(th -> th.enabled(true)); // 加上可以突破10000的限制

            if (queryBuilder != null) {
                builder.query(queryBuilder);
            }

            //排序
            this.mySortQuery(builder, targetVo);

            if (this.needPage(targetVo)) {
                builder.from(resultVo.getStartNum())
                        .size(resultVo.getPageSize());
            } else {
                builder.size(100);
            }

            SearchResponse<Void> response = client.search(builder.build(), Void.class);

            long total = (response.hits().total() == null) ? 0L : response.hits().total().value();
            resultVo.setRowNum((int) total);

            // 提取符合条件的 id 列表
            List<String> idList = new ArrayList<>();
            for (Hit<Void> hit : response.hits().hits()) {
                idList.add(hit.id());
            }
            resultVo.setIdList(idList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return resultVo;
    }
}
