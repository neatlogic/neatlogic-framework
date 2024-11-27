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
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.dao.mapper.ElasticsearchMapper;
import neatlogic.framework.dto.ElasticsearchVo;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@RootComponent
public class ElasticSearchManager {
    @Resource
    private ElasticsearchMapper elasticsearchMapper;

    private static final Map<String, ElasticsearchClient> elasticSearchClientMap = new HashMap<>();

    @PostConstruct
    public void init() {
        List<ElasticsearchVo> elasticsearchList = elasticsearchMapper.getAllActiveTenantElasticsearch();
        for (ElasticsearchVo elasticsearch : elasticsearchList) {
            if (!elasticSearchClientMap.containsKey(elasticsearch.getTenantUuid())) {
                RestClient restClient = RestClient
                        .builder(HttpHost.create(elasticsearch.getHost()))
                        /*.setDefaultHeaders(new Header[]{
                                new BasicHeader("Authorization", "ApiKey " + elasticsearch.getPasswordPlain())
                        })*/
                        .build();

                ElasticsearchTransport transport = new RestClientTransport(
                        restClient, new JacksonJsonpMapper());

                ElasticsearchClient esClient = new ElasticsearchClient(transport);
                elasticSearchClientMap.put(elasticsearch.getTenantUuid(), esClient);
            }
        }
    }


    public static ElasticsearchClient getClient() {
        return elasticSearchClientMap.get(TenantContext.get().getTenantUuid());
    }
}
