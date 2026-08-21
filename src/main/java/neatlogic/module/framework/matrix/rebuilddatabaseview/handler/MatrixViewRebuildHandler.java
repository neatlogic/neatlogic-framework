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

package neatlogic.module.framework.matrix.rebuilddatabaseview.handler;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.batch.BatchRunner;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.dao.mapper.SchemaMapper;
import neatlogic.framework.matrix.constvalue.MatrixType;
import neatlogic.framework.matrix.core.MatrixDataSourceHandlerFactory;
import neatlogic.framework.matrix.dao.mapper.MatrixMapper;
import neatlogic.framework.matrix.dto.MatrixViewVo;
import neatlogic.framework.matrix.dto.MatrixVo;
import neatlogic.framework.rebuilddatabaseview.core.IRebuildDataBaseView;
import neatlogic.framework.rebuilddatabaseview.core.ViewStatusInfo;
import neatlogic.module.framework.matrix.handler.ViewDataSourceHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

//@Component
public class MatrixViewRebuildHandler implements IRebuildDataBaseView {

    @Resource
    private MatrixMapper matrixMapper;
    @Resource
    private SchemaMapper schemaMapper;

    @Override
    public String getDescription() {
        return "重建视图类型矩阵的视图";
    }

    @Override
    public List<ViewStatusInfo> createViewIfNotExists() {
        List<ViewStatusInfo> resultList = new ArrayList<>();
        int rowNum = matrixMapper.getMatrixViewCount();
        if (rowNum > 0) {
            ViewDataSourceHandler viewDataSourceHandler = (ViewDataSourceHandler) MatrixDataSourceHandlerFactory.getHandler("view");// MatrixType.VIEW.getValue()
            BasePageVo searchVo = new BasePageVo();
            searchVo.setRowNum(rowNum);
            searchVo.setPageSize(100);
            int pageCount = searchVo.getPageCount();
            for (int currentPage = 1; currentPage <= pageCount; currentPage++) {
                searchVo.setCurrentPage(currentPage);
                List<MatrixViewVo> matrixViewList = matrixMapper.getMatrixViewList(searchVo);
                if (CollectionUtils.isNotEmpty(matrixViewList)) {
                    List<String> uuidList = matrixViewList.stream().map(MatrixViewVo::getMatrixUuid).collect(Collectors.toList());
                    List<MatrixVo> matrixList = matrixMapper.getMatrixListByUuidList(uuidList);
                    Map<String, String> uuidToNameMap = matrixList.stream().collect(Collectors.toMap(MatrixVo::getUuid, MatrixVo::getName));
                    for (MatrixViewVo matrixViewVo : matrixViewList) {
                        String matrixUuid = matrixViewVo.getMatrixUuid();
                        String tableType = schemaMapper.checkTableOrViewIsExists(TenantContext.get().getDataDbName(), "matrix_" + matrixUuid);
                        if (Objects.equals(tableType, "VIEW")) {
                            continue;
                        }
                        String matrixName = uuidToNameMap.get(matrixUuid);
                        ViewStatusInfo viewStatusInfo = new ViewStatusInfo();
                        viewStatusInfo.setName("matrix_" + matrixUuid);
                        viewStatusInfo.setLabel(matrixName);
                        try {
                            viewDataSourceHandler.buildView(matrixUuid, matrixName, matrixViewVo.getXml());
                            viewStatusInfo.setStatus(ViewStatusInfo.Status.SUCCESS.toString());
                            if (StringUtils.isNotBlank(matrixViewVo.getError())) {
                                matrixViewVo.setError(null);
                                matrixMapper.updateMatrixViewErrorByMatrixUuid(matrixViewVo);
                            }
                        } catch (Exception e) {
                            viewStatusInfo.setStatus(ViewStatusInfo.Status.FAILURE.toString());
                            viewStatusInfo.setError(e.getMessage());
                            matrixViewVo.setError(e.getMessage());
                            matrixMapper.updateMatrixViewErrorByMatrixUuid(matrixViewVo);
                        }
                        resultList.add(viewStatusInfo);
                    }
                }
            }
        }
        return resultList;
    }

    @Override
    public List<ViewStatusInfo> createOrReplaceView() {
        List<ViewStatusInfo> resultList = Collections.synchronizedList(new ArrayList<>());
        int rowNum = matrixMapper.getMatrixViewCount();
        if (rowNum > 0) {
            ViewDataSourceHandler viewDataSourceHandler = (ViewDataSourceHandler) MatrixDataSourceHandlerFactory.getHandler("view");// MatrixType.VIEW.getValue()
            BasePageVo searchVo = new BasePageVo();
            searchVo.setRowNum(rowNum);
            searchVo.setPageSize(100);
            int pageCount = searchVo.getPageCount();
            for (int currentPage = 1; currentPage <= pageCount; currentPage++) {
                searchVo.setCurrentPage(currentPage);
                List<MatrixViewVo> matrixViewList = matrixMapper.getMatrixViewList(searchVo);
                if (CollectionUtils.isNotEmpty(matrixViewList)) {
                    List<String> uuidList = matrixViewList.stream().map(MatrixViewVo::getMatrixUuid).collect(Collectors.toList());
                    List<MatrixVo> matrixList = matrixMapper.getMatrixListByUuidList(uuidList);
                    Map<String, String> uuidToNameMap = matrixList.stream().collect(Collectors.toMap(MatrixVo::getUuid, MatrixVo::getName));
                    BatchRunner<MatrixViewVo> runner = new BatchRunner<>();
                    runner.execute(matrixViewList, 5, (threadIndex, dataIndex, matrixViewVo) -> {
                        long startTime = System.currentTimeMillis();
                        String matrixUuid = matrixViewVo.getMatrixUuid();
                        String matrixName = uuidToNameMap.get(matrixUuid);
                        ViewStatusInfo viewStatusInfo = new ViewStatusInfo();
                        viewStatusInfo.setName("matrix_" + matrixUuid);
                        viewStatusInfo.setLabel(matrixName);
                        try {
                            viewDataSourceHandler.buildView(matrixUuid, matrixName, matrixViewVo.getXml());
                            viewStatusInfo.setStatus(ViewStatusInfo.Status.SUCCESS.toString());
                        } catch (Exception e) {
                            viewStatusInfo.setStatus(ViewStatusInfo.Status.FAILURE.toString());
                            viewStatusInfo.setError(e.getMessage());
                        }
                        viewStatusInfo.setTimeCost(System.currentTimeMillis() - startTime);
                        resultList.add(viewStatusInfo);
                    }, "REBUILD-DATABASE-VIEW-FOR-MATRIXVIEW");
//                    for (MatrixViewVo matrixViewVo : matrixViewList) {
//                        String matrixUuid = matrixViewVo.getMatrixUuid();
//                        String matrixName = uuidToNameMap.get(matrixUuid);
//                        ViewStatusInfo viewStatusInfo = new ViewStatusInfo();
//                        viewStatusInfo.setName("matrix_" + matrixUuid);
//                        viewStatusInfo.setLabel(matrixName);
//                        try {
//                            viewDataSourceHandler.buildView(matrixUuid, matrixName, matrixViewVo.getXml());
//                            viewStatusInfo.setStatus(ViewStatusInfo.Status.SUCCESS.toString());
//                        } catch (Exception e) {
//                            viewStatusInfo.setStatus(ViewStatusInfo.Status.FAILURE.toString());
//                            viewStatusInfo.setError(e.getMessage());
//                        }
//                        resultList.add(viewStatusInfo);
//                    }
                }
            }
        }
        return resultList;
    }

    @Override
    public int getSort() {
        return 2;
    }
}
