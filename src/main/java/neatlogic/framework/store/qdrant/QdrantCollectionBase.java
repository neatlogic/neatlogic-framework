/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the Sustainable Use License (SUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.store.qdrant;

import io.qdrant.client.PointIdFactory;
import io.qdrant.client.grpc.Points;
import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.asynchronization.threadpool.CachedThreadPool;
import neatlogic.framework.dao.mapper.EmbeddingAuditMapper;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.exception.qdrant.QdrantDeleteCollectionException;
import neatlogic.framework.fulltextindex.dao.mapper.FullTextIndexRebuildAuditMapper;
import neatlogic.framework.fulltextindex.dto.fulltextindex.FullTextIndexRebuildAuditVo;
import neatlogic.framework.fulltextindex.enums.FullTextIndexHandlerType;
import neatlogic.framework.fulltextindex.enums.Status;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class QdrantCollectionBase<T> implements IQdrantCollection<T> {
    private final Logger logger = LoggerFactory.getLogger(QdrantCollectionBase.class);
    @Resource
    private FullTextIndexRebuildAuditMapper fullTextIndexRebuildAuditMapper;
    @Resource
    private EmbeddingAuditMapper embeddingAuditMapper;

    protected final String getTenantCollectionName() {
        return (TenantContext.get().getTenantUuid() + "_" + this.getName()).toLowerCase();
    }

    @Override
    public final void initCollection() {
        if (!isCollectionExists()) {
            try {
                myInitCollection();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    @Override
    public final void deleteCollection() {
        if (isCollectionExists()) {
            try {
                QdrantClientFactory.getClient()
                        .deleteCollectionAsync(this.getTenantCollectionName())
                        .get(30, TimeUnit.SECONDS);
            } catch (Exception e) {
                throw new QdrantDeleteCollectionException(e);
            }
        }
    }

    protected abstract void myInitCollection() throws Exception;

    protected boolean isCollectionExists() {
        try {
            return QdrantClientFactory.getClient().collectionExistsAsync(this.getTenantCollectionName()).get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }


    @Override
    public final void upsertPoint(Long targetId, T target) throws ExecutionException, InterruptedException, TimeoutException {
        if (targetId == null || target == null) {
            return;
        }
        List<Points.PointStruct> points = generatePoint(target);
        if (CollectionUtils.isEmpty(points)) {
            return;
        }
        QdrantClientFactory.getClient().upsertAsync(this.getTenantCollectionName(), points).get(30, TimeUnit.SECONDS);
        embeddingAuditMapper.insertEmbeddingAudit(targetId, this.getName());
    }


    protected abstract List<Points.PointStruct> generatePoint(T target) throws ExecutionException, InterruptedException, TimeoutException;

    @Override
    public int getPointCount() {
        try {
            Long pointCount = QdrantClientFactory.getClient()
                    .countAsync(this.getTenantCollectionName())
                    .get(30, TimeUnit.SECONDS);
            return pointCount == null ? 0 : pointCount.intValue();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public final void rebuildPoint(boolean isAll) {
        FullTextIndexRebuildAuditVo auditVo = new FullTextIndexRebuildAuditVo();
        auditVo.setType(this.getName());
        auditVo.setEditor(UserContext.get().getUserUuid(true));
        auditVo.setStatus(Status.DOING.getValue());
        auditVo.setHandler(FullTextIndexHandlerType.QDRANT.getValue());
        fullTextIndexRebuildAuditMapper.insertFullTextIndexRebuildAudit(auditVo);
        if (isAll) {
            this.deleteCollection();
            this.initCollection();
        }
        CachedThreadPool.execute(new NeatLogicThread("QDRANT-COLLECTION-REBUILD-" + this.getName()) {
            @Override
            protected void execute() {
                try {
                    myRebuildPoint(isAll);
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


    protected abstract void myRebuildPoint(boolean isAll);


    @Override
    public final void deletePoint(Long id) {
        try {
            QdrantClientFactory.getClient()
                    .deleteAsync(
                            this.getTenantCollectionName(),
                            List.of(PointIdFactory.id(id))
                    )
                    .get(30, TimeUnit.SECONDS);
            embeddingAuditMapper.deleteEmbeddingAudit(id, this.getName());
        } catch (Exception e) {
            logger.error("Qdrant deleteById error: {}", e.getMessage(), e);
        }
    }


}
