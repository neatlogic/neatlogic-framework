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
import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.dto.ElasticsearchVo;
import neatlogic.framework.exception.elasticsearch.ElasticSearchCreateDocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}
