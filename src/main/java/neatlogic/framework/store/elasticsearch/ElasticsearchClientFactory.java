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
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.dao.mapper.ElasticsearchMapper;
import neatlogic.framework.dto.ElasticsearchVo;
import neatlogic.framework.exception.elasticsearch.ElasticSearchHostNotFoundException;
import neatlogic.framework.util.SpringContextUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RootComponent
public class ElasticsearchClientFactory extends ModuleInitializedListenerBase {


    private static final Map<String, ElasticsearchClient> elasticSearchClientMap = new HashMap<>();


    public static ElasticsearchClient getClient() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        if (!elasticSearchClientMap.containsKey(TenantContext.get().getTenantUuid())) {
            ElasticsearchVo elasticsearch = SpringContextUtil.getBean(ElasticsearchMapper.class).getTenantElasticsearchByTenantUuid(TenantContext.get().getTenantUuid());
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
                    SSLContext sslContext = SSLContexts.custom()
                            .loadTrustMaterial(null, TrustAllStrategy.INSTANCE)
                            .build();

                    RestClient restClient = RestClient
                            .builder(httpHosts.toArray(new HttpHost[0]))
                            .setHttpClientConfigCallback(httpClientBuilder -> {
                                httpClientBuilder.setSSLContext(sslContext);
                                httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
                                httpClientBuilder.disableAuthCaching();
                                httpClientBuilder.setDefaultIOReactorConfig(IOReactorConfig.custom()
                                        .setIoThreadCount(Runtime.getRuntime().availableProcessors())
                                        .setSoKeepAlive(true)
                                        .build());
                                /*es7*
                                httpClientBuilder.setDefaultHeaders(Collections.singletonList(
                                        new BasicHeader(
                                                HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON)));

                                 */
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


    @Override
    protected void myInit() {
    }
}
