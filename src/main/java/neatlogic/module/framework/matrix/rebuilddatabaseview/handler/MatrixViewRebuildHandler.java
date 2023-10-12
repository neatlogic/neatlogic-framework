/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.module.framework.matrix.rebuilddatabaseview.handler;

import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.matrix.constvalue.MatrixType;
import neatlogic.framework.matrix.core.MatrixDataSourceHandlerFactory;
import neatlogic.framework.matrix.dao.mapper.MatrixMapper;
import neatlogic.framework.matrix.dto.MatrixViewVo;
import neatlogic.framework.matrix.dto.MatrixVo;
import neatlogic.framework.rebuilddatabaseview.core.IRebuildDataBaseView;
import neatlogic.framework.rebuilddatabaseview.core.ViewStatusInfo;
import neatlogic.module.framework.matrix.handler.ViewDataSourceHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MatrixViewRebuildHandler implements IRebuildDataBaseView {

    @Resource
    private MatrixMapper matrixMapper;

    @Override
    public String getDescription() {
        return "重建视图类型矩阵的视图";
    }

    @Override
    public List<ViewStatusInfo> execute() {
        List<ViewStatusInfo> resultList = new ArrayList<>();
        int rowNum = matrixMapper.getMatrixViewCount();
        if (rowNum == 0) {
            return resultList;
        }
        ViewDataSourceHandler viewDataSourceHandler = (ViewDataSourceHandler) MatrixDataSourceHandlerFactory.getHandler(MatrixType.VIEW.getValue());
        BasePageVo searchVo = new BasePageVo();
        searchVo.setRowNum(rowNum);
        searchVo.setPageSize(100);
        int pageCount = searchVo.getPageCount();
        for (int currentPage = 1; currentPage <= pageCount; currentPage++) {
            searchVo.setCurrentPage(currentPage);
            List<MatrixViewVo> matrixViewList = matrixMapper.getMatrixViewList(searchVo);
            if (CollectionUtils.isEmpty(matrixViewList)) {
                continue;
            }
            List<String> uuidList = matrixViewList.stream().map(MatrixViewVo::getMatrixUuid).collect(Collectors.toList());
            List<MatrixVo> matrixList = matrixMapper.getMatrixListByUuidList(uuidList);
            Map<String, String> uuidToNameMap = matrixList.stream().collect(Collectors.toMap(MatrixVo::getUuid, MatrixVo::getName));
            for (MatrixViewVo matrixViewVo : matrixViewList) {
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
                resultList.add(viewStatusInfo);
            }
        }
        return resultList;
    }

    @Override
    public int getSort() {
        return 2;
    }
}
