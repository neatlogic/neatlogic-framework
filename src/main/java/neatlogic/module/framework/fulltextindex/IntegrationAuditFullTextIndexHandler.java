/*
 * Copyright (C) 2025  深圳极向量科技有限公司 All Rights Reserved.
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
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class IntegrationAuditFullTextIndexHandler extends FullTextIndexHandlerBase {

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
//        IntegrationAuditVo integrationAuditVo = integrationMapper.getIntegrationAuditById(fullTextIndexVo.getTargetId());
//        if (integrationAuditVo != null && StringUtils.isNotBlank(integrationAuditVo.getParamFilePath())) {
//            IntegrationVo integrationVo = integrationMapper.getIntegrationByUuid(integrationAuditVo.getIntegrationUuid());
//            if (integrationVo != null) {
//                JSONObject config = integrationVo.getConfig();
//                JSONObject param = config.getJSONObject("param");
//                if (MapUtils.isNotEmpty(param)) {
//                    JSONArray paramList = param.getJSONArray("paramList");
//                    if (CollectionUtils.isNotEmpty(paramList)) {
//                        List<String> searchAbleParamNameList = new ArrayList<>();
//                        for (int i = 0; i < paramList.size(); i++) {
//                            JSONObject paramObj = paramList.getJSONObject(i);
//                            String name = paramObj.getString("name");
//                            String mode = paramObj.getString("mode");
//                            Integer isSearchAble = paramObj.getInteger("isSearchAble");
//                            if (StringUtils.isNotBlank(name) && Objects.equals(mode, "input") && Objects.equals(isSearchAble, 1)) {
//                                searchAbleParamNameList.add(name);
//                            }
//                        }
//                        if (CollectionUtils.isNotEmpty(searchAbleParamNameList)) {
//                            List<String> paramValueList = new ArrayList<>();
//                            String content = getContent(integrationAuditVo.getParamFilePath());
//                            if (StringUtils.isNotBlank(content) && content.startsWith("{") && content.endsWith("}")) {
//                                JSONObject paramObj = JSONObject.parseObject(content);
//                                for (String paramName : searchAbleParamNameList) {
//                                    Object paramValue = paramObj.get(paramName);
//                                    if (paramValue != null) {
//                                        paramValueList.add(paramValue.toString());
//                                    }
//                                }
//                            }
//                            if (CollectionUtils.isNotEmpty(paramValueList)) {
//                                fullTextIndexVo.addFieldContent("param", new FullTextIndexVo.WordVo(String.join(",", paramValueList)));
//                            }
//                        }
//                    }
//                }
//            }
//        }
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
            for (int currentPage = 1; currentPage < pageCount; currentPage++) {
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
        AuditFilePathVo auditFilePathVo = new AuditFilePathVo(filePath);
        IFileCrossoverService fileCrossoverService = CrossoverServiceFactory.getApi(IFileCrossoverService.class);
        if (Objects.equals(auditFilePathVo.getServerId(), Config.SCHEDULE_SERVER_ID)) {
            JSONObject jsonObj = fileCrossoverService.readLocalFile(auditFilePathVo.getPath(), auditFilePathVo.getStartIndex(), auditFilePathVo.getOffset());
            return jsonObj.getString("content");
        } else {
            JSONObject paramObj = new JSONObject();
            paramObj.put("filePath", filePath);
            JSONObject jsonObj =  fileCrossoverService.readRemoteFile(paramObj, auditFilePathVo.getServerId());
            return jsonObj.getString("content");
        }
    }
}
