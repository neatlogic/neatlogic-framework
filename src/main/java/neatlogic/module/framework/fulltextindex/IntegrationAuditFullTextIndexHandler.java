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

package neatlogic.module.framework.fulltextindex;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.crossover.CrossoverServiceFactory;
import neatlogic.framework.crossover.IFileCrossoverService;
import neatlogic.framework.file.dto.AuditFilePathVo;
import neatlogic.framework.fulltextindex.core.FullTextIndexHandlerBase;
import neatlogic.framework.fulltextindex.core.IFullTextIndexType;
import neatlogic.framework.fulltextindex.dto.fulltextindex.FullTextIndexTypeVo;
import neatlogic.framework.fulltextindex.dto.fulltextindex.FullTextIndexVo;
import neatlogic.framework.fulltextindex.dto.globalsearch.DocumentVo;
import neatlogic.framework.fulltextindex.enums.FrameworkFullTextIndexType;
import neatlogic.framework.integration.crossover.IntegrationCrossoverService;
import neatlogic.framework.integration.dao.mapper.IntegrationMapper;
import neatlogic.framework.integration.dto.IntegrationAuditVo;
import neatlogic.framework.integration.dto.IntegrationVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class IntegrationAuditFullTextIndexHandler extends FullTextIndexHandlerBase {

    private final Logger logger = LoggerFactory.getLogger(IntegrationAuditFullTextIndexHandler.class);

    @Resource
    private IntegrationMapper integrationMapper;

    @Override
    protected String getModuleId() {
        return "framework";
    }

    @Override
    protected void myCreateIndex(FullTextIndexVo fullTextIndexVo) {
        JSONObject dataObj = fullTextIndexVo.getDataObj();
        if (MapUtils.isNotEmpty(dataObj)) {
            String value = dataObj.getString("param");
            if (StringUtils.isNotBlank(value)) {
                fullTextIndexVo.addFieldContent("param", new FullTextIndexVo.WordVo(value));
            }
        }
    }

    @Override
    protected void myMakeupDocument(DocumentVo documentVo) {

    }

    @Override
    protected void myRebuildIndex(FullTextIndexTypeVo fullTextIndexTypeVo) {
        IntegrationVo searchVo = new IntegrationVo();
        int rowNum = integrationMapper.searchIntegrationCount(searchVo);
        if (rowNum > 0) {
            searchVo.setRowNum(rowNum);
            searchVo.setPageSize(100);
            Integer pageCount = searchVo.getPageCount();
            for (int currentPage = 1; currentPage <= pageCount; currentPage++) {
                searchVo.setCurrentPage(currentPage);
                List<IntegrationVo> integrationList = integrationMapper.searchIntegration(searchVo);
                for (IntegrationVo integrationVo : integrationList) {
                    IntegrationCrossoverService integrationCrossoverService = CrossoverServiceFactory.getApi(IntegrationCrossoverService.class);
                    List<String> searchAbleInputParamNameList = integrationCrossoverService.getSearchAbleInputParamNameList(integrationVo);
                    if (CollectionUtils.isNotEmpty(searchAbleInputParamNameList)) {
                        Long startId = 0L;
                        List<Long> notIndexApiAuditIdList = integrationMapper.getNotIndexIntegrationAuditIdList(integrationVo.getUuid(), startId, fullTextIndexTypeVo.getType(), 100);
                        while (CollectionUtils.isNotEmpty(notIndexApiAuditIdList)) {
                            List<IntegrationAuditVo> integrationAuditList = integrationMapper.getIntegrationAuditListByIdList(notIndexApiAuditIdList);
                            for (IntegrationAuditVo integrationAuditVo : integrationAuditList) {
                                if (StringUtils.isNotBlank(integrationAuditVo.getParamFilePath())) {
                                    String content = getContent(integrationAuditVo.getParamFilePath());
                                    if (StringUtils.isNotBlank(content) && content.startsWith("{") && content.endsWith("}")) {
                                        List<String> fullIndexParamValueList = new ArrayList<>();
                                        JSONObject requestParamObj = JSONObject.parseObject(content);
                                        for (String paramName : searchAbleInputParamNameList) {
                                            Object paramValue = requestParamObj.get(paramName);
                                            if (paramValue != null) {
                                                fullIndexParamValueList.add(paramValue.toString());
                                            }
                                        }
                                        if (CollectionUtils.isNotEmpty(fullIndexParamValueList)) {
                                            JSONObject dataObj = new JSONObject();
                                            dataObj.put("param", String.join(",", fullIndexParamValueList));
                                            this.createIndex(integrationAuditVo.getId(), dataObj, true);
                                        }
                                    }
                                }
                            }
                            startId = notIndexApiAuditIdList.get(notIndexApiAuditIdList.size() - 1);
                            notIndexApiAuditIdList = integrationMapper.getNotIndexIntegrationAuditIdList(integrationVo.getUuid(), startId, fullTextIndexTypeVo.getType(), 100);
                        }
                    }
                }
            }
        }
    }

    @Override
    public IFullTextIndexType getType() {
        return FrameworkFullTextIndexType.INTEGRATION_AUDIT;
    }

    private String getContent(String filePath) {
        try {
            AuditFilePathVo auditFilePathVo = new AuditFilePathVo(filePath);
            IFileCrossoverService fileCrossoverService = CrossoverServiceFactory.getApi(IFileCrossoverService.class);
            if (Objects.equals(auditFilePathVo.getServerId(), Config.SCHEDULE_SERVER_ID)) {
                JSONObject jsonObj = fileCrossoverService.readLocalFile(auditFilePathVo.getPath(), auditFilePathVo.getStartIndex(), auditFilePathVo.getOffset());
                return jsonObj.getString("content");
            } else {
                JSONObject paramObj = new JSONObject();
                paramObj.put("filePath", filePath);
                JSONObject jsonObj = fileCrossoverService.readRemoteFile(paramObj, auditFilePathVo.getServerId());
                return jsonObj.getString("content");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
