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
