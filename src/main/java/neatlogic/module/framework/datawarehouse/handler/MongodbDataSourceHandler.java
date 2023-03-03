/*
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. 
 */

package neatlogic.module.framework.datawarehouse.handler;

import neatlogic.framework.common.util.PageUtil;
import neatlogic.framework.datawarehouse.core.DataSourceServiceHandlerBase;
import neatlogic.framework.datawarehouse.dto.*;
import neatlogic.framework.datawarehouse.exceptions.ReportDataSourceSyncException;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class MongodbDataSourceHandler extends DataSourceServiceHandlerBase {
    static Logger logger = LoggerFactory.getLogger(MysqlDataSourceHandler.class);
    static final int batchSize = 100;
    @Resource
    MongoTemplate mongoTemplate;

    @Override
    public String getHandler() {
        return "mongodb";
    }

    @Override
    public void mySyncData(DataSourceVo dataSourceVo, DataSourceAuditVo reportDataSourceAuditVo) {
        try {
            List<SelectVo> selectList = getSqlFromDataSource(dataSourceVo);
            for (SelectVo select : selectList) {
                String sqlText = select.getSql();
                int rowNum = 0;
                Document bsonCmd = Document.parse(sqlText);
                Document bsonCmdCount = new Document("aggregate", bsonCmd.get("aggregate"));
                //先获取count 看是否需要分页
                List<Document> pipelineList = bsonCmd.getList("pipeline", Document.class);
                List<Document> pipelineCountList = new ArrayList<>();
                for (Document pipeline : pipelineList) {
                    if (pipeline.containsKey("$match")) {
                        pipelineCountList.add(new Document("$match", pipeline.get("$match")));
                        pipelineCountList.add(new Document("$count", "count"));
                    }
                }
                bsonCmdCount.put("pipeline", pipelineCountList);
                bsonCmdCount.put("cursor", new Document("batchSize", batchSize));
                pipelineCountList.add(new Document("$addFields", new Document("count", "$count")));
                Document resultCountDoc = mongoTemplate.executeCommand(bsonCmdCount);
                if (resultCountDoc.containsKey("cursor")) {
                    Document cursorCountResultDoc = ((Document) resultCountDoc.get("cursor"));
                    for (Document documentCount : cursorCountResultDoc.getList("firstBatch", Document.class)) {
                        rowNum = documentCount.getInteger("count");
                        break;
                    }
                }
                if (rowNum > 0) {
                    //设置默认获取数
                    bsonCmd.put("cursor", new Document("batchSize", batchSize));
                    for (int i = 0; i < PageUtil.getPageCount(rowNum, batchSize); i++) {
                        //设置跳过数量
                        if(i == 0){
                            pipelineList.add(new Document("$skip", 0));
                        }
                        for (Document pipeline : pipelineList) {
                            if (pipeline.containsKey("$skip")) {
                                pipeline.put("$skip", batchSize * i);
                            }
                        }
                        Document resultDoc = mongoTemplate.executeCommand(bsonCmd);
                        if (resultDoc.containsKey("cursor")) {
                            Document cursorResultDoc = ((Document) resultDoc.get("cursor"));
                            for (Document document : cursorResultDoc.getList("firstBatch", Document.class)) {
                                DataSourceDataVo reportDataSourceDataVo = new DataSourceDataVo(dataSourceVo.getId());
                                reportDataSourceDataVo.setExpireMinute(dataSourceVo.getExpireMinute());
                                List<DataSourceFieldVo> aggregateFieldList = new ArrayList<>();
                                List<DataSourceFieldVo> keyFieldList = new ArrayList<>();
                                for (DataSourceFieldVo fieldVo : dataSourceVo.getFieldList()) {
                                    if (document.containsKey(fieldVo.getName())) {
                                        Object v = document.get(fieldVo.getName());
                                        fieldVo.setValue(v != null ? v : "");//把所有的null值都转成空字符串
                                    }
                                    reportDataSourceDataVo.addField(fieldVo);
                                    if (StringUtils.isNotBlank(fieldVo.getAggregate())) {
                                        aggregateFieldList.add(fieldVo);
                                    }
                                    if (fieldVo.getIsKey().equals(1)) {
                                        keyFieldList.add(fieldVo);
                                    }
                                }
                                aggregateAndInsertData(aggregateFieldList, keyFieldList, reportDataSourceDataVo, reportDataSourceAuditVo);
                            }
                        }
                    }
                }
            }
        } catch (DocumentException e) {
            logger.error(e.getMessage(), e);
            reportDataSourceAuditVo.setError(e.getMessage());
            throw new ReportDataSourceSyncException(dataSourceVo, e);
        }
    }
}
