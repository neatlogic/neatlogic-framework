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
import co.elastic.clients.util.ContentType;
import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.dao.mapper.ElasticsearchMapper;
import neatlogic.framework.dto.ElasticsearchVo;
import neatlogic.framework.exception.elasticsearch.ElasticSearchHostNotFoundException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@RootComponent
public class ElasticsearchClientFactory extends ModuleInitializedListenerBase {
    static Logger logger = LoggerFactory.getLogger(ElasticsearchClientFactory.class);
    private static ElasticsearchMapper elasticsearchMapper;
    private static final Map<String, ElasticsearchVo> elasticsearchMap = new HashMap<>();

    @Autowired
    public ElasticsearchClientFactory(ElasticsearchMapper _elasticsearchMapper) {
        elasticsearchMapper = _elasticsearchMapper;
    }

    private static final Map<String, ElasticsearchClient> elasticSearchClientMap = new HashMap<>();


    public static ElasticsearchClient getClient() {
        if (!elasticSearchClientMap.containsKey(TenantContext.get().getTenantUuid())) {
            ElasticsearchVo elasticsearch = getElasticsearchVo();
            if (elasticsearch != null) {
                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                if (StringUtils.isNotBlank(elasticsearch.getUsername()) && StringUtils.isNotBlank(elasticsearch.getPasswordPlain())) {
                    credentialsProvider.setCredentials(AuthScope.ANY,
                            new UsernamePasswordCredentials(elasticsearch.getUsername(), elasticsearch.getPasswordPlain()));
                }
                List<HttpHost> httpHosts = new ArrayList<>();
                if (StringUtils.isNotBlank(elasticsearch.getHost())) {
                    String[] hostArray = elasticsearch.getHost().split(",");
                    for (String host : hostArray) {
                        if (StringUtils.isNotBlank(host)) {
                            httpHosts.add(HttpHost.create(host));
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(httpHosts)) {
                    RestClient restClient = RestClient
                            .builder(httpHosts.toArray(new HttpHost[0]))
                            .setHttpClientConfigCallback(httpClientBuilder -> {
                                httpClientBuilder.disableAuthCaching();
                                httpClientBuilder.setDefaultHeaders(Collections.singletonList(
                                        new BasicHeader(
                                                HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON)));
                                httpClientBuilder.addInterceptorLast((HttpResponseInterceptor)
                                        (response, context) ->
                                                response.addHeader("X-Elastic-Product", "Elasticsearch"));

                                if (elasticsearch.getConfig().containsKey("maxConnPerRoute")) {
                                    int maxConnPerRoute = elasticsearch.getConfig().getIntValue("maxConnPerRoute");
                                    if (maxConnPerRoute > 0) {
                                        httpClientBuilder.setMaxConnTotal(httpHosts.size() * maxConnPerRoute);
                                        httpClientBuilder.setMaxConnPerRoute(maxConnPerRoute);
                                    }
                                }
                                return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                            }).build();

                    ElasticsearchTransport transport = new RestClientTransport(
                            restClient, new JacksonJsonpMapper());

                    ElasticsearchClient esClient = new ElasticsearchClient(transport);
                    elasticSearchClientMap.put(elasticsearch.getTenantUuid(), esClient);

                    List<IElasticsearchIndex> indexList = ElasticsearchIndexFactory.getAllIndex();
                    for (IElasticsearchIndex index : indexList) {
                        index.createIndex();
                    }
                } else {
                    throw new ElasticSearchHostNotFoundException();
                }
            }
        }
        return elasticSearchClientMap.get(TenantContext.get().getTenantUuid());
    }

    @Override
    protected void onInitialized(NeatLogicWebApplicationContext context) {

    }

    public static ElasticsearchVo getElasticsearchVo() {
        return elasticsearchMap.get(TenantContext.get().getTenantUuid());
    }

    @Override
    protected void myInit() {
        List<ElasticsearchVo> elasticsearchVoList = elasticsearchMapper.getAllTenantElasticsearch();
        for (ElasticsearchVo elasticsearchVo : elasticsearchVoList) {
            elasticsearchMap.put(elasticsearchVo.getTenantUuid(), elasticsearchVo);
        }
    }
}
