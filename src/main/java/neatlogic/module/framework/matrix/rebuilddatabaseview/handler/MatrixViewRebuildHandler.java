/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

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
