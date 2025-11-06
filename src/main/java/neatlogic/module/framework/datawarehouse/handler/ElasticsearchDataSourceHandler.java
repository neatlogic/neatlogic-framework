/*Copyright (C) 2023  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.module.framework.datawarehouse.handler;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jakarta.json.stream.JsonParser;
import neatlogic.framework.datawarehouse.core.DataSourceServiceHandlerBase;
import neatlogic.framework.datawarehouse.dao.mapper.DataWarehouseDataSourceMapper;
import neatlogic.framework.datawarehouse.dto.*;
import neatlogic.framework.datawarehouse.exceptions.ReportDataSourceSyncException;
import neatlogic.framework.store.elasticsearch.ElasticsearchClientFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ElasticsearchDataSourceHandler extends DataSourceServiceHandlerBase {
    static Logger logger = LoggerFactory.getLogger(ElasticsearchDataSourceHandler.class);
    static final int batchSize = 100;
    @Resource
    private DataWarehouseDataSourceMapper dataSourceMapper;

    @Override
    public String getHandler() {
        return "elasticsearch";
    }

    @Override
    public void mySyncData(DataSourceVo dataSourceVo, DataSourceAuditVo reportDataSourceAuditVo) {
        try {
            List<SelectVo> selectList = getSqlFromDataSource(dataSourceVo);
            for (SelectVo select : selectList) {
                Map<String, Object> paramMap = select.getParamMap();
                String queryText = select.getSql();
                for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                    queryText = queryText.replaceAll("#\\{" + entry.getKey() + "}", entry.getValue().toString());
                }

                // 解析 JSON
                JSONObject queryObj = JSON.parseObject(queryText);
                String index = queryObj.getString("index");
                if (StringUtils.isBlank(index)) {
                    throw new ReportDataSourceSyncException(dataSourceVo, new RuntimeException("index未定义"));
                }

                ElasticsearchClient client = ElasticsearchClientFactory.getClient();
                int total = 0;
                int from = 0;

                // 默认分页
                int size = queryObj.getInteger("size") != null ? queryObj.getInteger("size") : batchSize;
                if (size > batchSize) size = batchSize;

                do {
                    queryObj.put("from", from);
                    queryObj.put("size", size);

                    // 构建请求
                    SearchRequest.Builder reqBuilder = new SearchRequest.Builder().index(index);

                    // 解析 query
                    if (queryObj.containsKey("query")) {
                        String queryJson = queryObj.getJSONObject("query").toJSONString();
                        try (InputStream is = new ByteArrayInputStream(queryJson.getBytes(StandardCharsets.UTF_8))) {
                            JsonpMapper mapper = new JacksonJsonpMapper();
                            JsonParser parser = mapper.jsonProvider().createParser(is);
                            Query query = new Query.Builder().withJson(parser, mapper).build();
                            reqBuilder.query(query);
                        }
                    }

                    // 解析 _source
                    if (queryObj.containsKey("_source")) {
                        List<String> srcFields = queryObj.getJSONArray("_source").toJavaList(String.class);
                        reqBuilder.source(s -> s.filter(f -> f.includes(srcFields)));
                    }

                    // 执行查询
                    SearchResponse<Map> resp = client.search(reqBuilder.build(), Map.class);
                    List<Hit<Map>> hits = resp.hits().hits();
                    if (hits.isEmpty()) break;

                    total += hits.size();

                    // 处理结果
                    for (Hit<Map> hit : hits) {
                        Map<String, Object> source = hit.source();
                        if (source == null) continue;

                        DataSourceDataVo reportDataSourceDataVo = new DataSourceDataVo(dataSourceVo.getId());
                        reportDataSourceDataVo.setExpireMinute(dataSourceVo.getExpireMinute());
                        List<DataSourceFieldVo> aggregateFieldList = new ArrayList<>();
                        List<DataSourceFieldVo> keyFieldList = new ArrayList<>();

                        if (CollectionUtils.isNotEmpty(dataSourceVo.getParamList())) {
                            for (DataSourceParamVo paramVo : dataSourceVo.getParamList()) {
                                if (source.containsKey(paramVo.getName().toLowerCase())) {
                                    Object v = source.get(paramVo.getName().toLowerCase());
                                    Long lv = null;
                                    try {
                                        lv = (Long) v;
                                    } catch (Exception ex) {
                                        logger.error(ex.getMessage(), ex);
                                    }
                                    if (lv != null) {
                                        if (paramVo.getCurrentValue() == null) {
                                            paramVo.setCurrentValue(lv);
                                        } else if (lv > paramVo.getCurrentValue()) {
                                            paramVo.setCurrentValue(lv);
                                        }
                                    }
                                }
                            }
                        }


                        for (DataSourceFieldVo fieldVo : dataSourceVo.getFieldList()) {
                            Object v = null;
                            if (fieldVo.getName().contains(".")) {
                                String[] names = fieldVo.getName().split("\\.");
                                if (source.containsKey(names[0]) && source.get(names[0]) instanceof Map) {
                                    Map<String, Object> sourceMap = (Map<String, Object>) source.get(names[0]);
                                    for (int i = 1; i < names.length; i++) {
                                        if (sourceMap.containsKey(names[i])) {
                                            if (source.get(names[i]) instanceof Map) {
                                                sourceMap = (Map<String, Object>) source.get(names[i]);
                                            } else {
                                                v = sourceMap.get(names[i]);
                                                break;
                                            }
                                        }
                                    }
                                }
                            } else {
                                v = source.get(fieldVo.getName());
                            }
                            fieldVo.setValue(v != null ? v : "");
                            reportDataSourceDataVo.addField(fieldVo);

                            if (StringUtils.isNotBlank(fieldVo.getAggregate())) {
                                aggregateFieldList.add(fieldVo);
                            }
                            if (Objects.equals(fieldVo.getIsKey(), 1)) {
                                keyFieldList.add(fieldVo);
                            }
                        }

                        aggregateAndInsertData(aggregateFieldList, keyFieldList, reportDataSourceDataVo, reportDataSourceAuditVo);
                    }

                    if (CollectionUtils.isNotEmpty(dataSourceVo.getParamList())) {
                        for (DataSourceParamVo param : dataSourceVo.getParamList()) {
                            dataSourceMapper.updateDataSourceParamCurrentValue(param);
                        }
                    }

                    from += size;
                    if (hits.size() < size) break;

                } while (true);

                logger.info("索引 {} 同步完成，共 {} 条记录", index, total);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            reportDataSourceAuditVo.setError(e.getMessage());
            throw new ReportDataSourceSyncException(dataSourceVo, e);
        }
    }
}
