/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.matrix.core;

import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.dependency.constvalue.CalleeType;
import codedriver.framework.dependency.core.DependencyManager;
import codedriver.framework.matrix.dao.mapper.MatrixMapper;
import codedriver.framework.matrix.dto.MatrixAttributeVo;
import codedriver.framework.matrix.dto.MatrixDataVo;
import codedriver.framework.matrix.dto.MatrixVo;
import codedriver.framework.matrix.exception.MatrixNotFoundException;
import codedriver.framework.matrix.exception.MatrixReferencedCannotBeDeletedException;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author linbq
 * @since 2021/11/5 10:26
 **/
public abstract class MatrixDataSourceHandlerBase implements IMatrixDataSourceHandler {

    protected static MatrixMapper matrixMapper;

    @Resource
    public void setMatrixMapper(MatrixMapper _matrixMapper) {
        matrixMapper = _matrixMapper;
    }

    @Override
    public MatrixVo saveMatrix(MatrixVo matrixVo) throws Exception {
        if (matrixMapper.checkMatrixIsExists(matrixVo.getUuid()) == 0) {
            matrixMapper.insertMatrix(matrixVo);
        } else {
            matrixMapper.updateMatrixNameAndLcu(matrixVo);
        }
        mySaveMatrix(matrixVo);
        return matrixVo;
    }

    protected abstract void mySaveMatrix(MatrixVo matrixVo) throws Exception;

    @Override
    public MatrixVo getMatrix(String uuid) {
        MatrixVo matrixVo = matrixMapper.getMatrixByUuid(uuid);
        if (matrixVo == null) {
            throw new MatrixNotFoundException(uuid);
        }
        myGetMatrix(matrixVo);
        return matrixVo;
    }

    protected abstract void myGetMatrix(MatrixVo matrixVo);

    @Override
    public void deleteMatrix(String uuid) {
        if (DependencyManager.getDependencyCount(CalleeType.MATRIX, uuid) > 0) {
            throw new MatrixReferencedCannotBeDeletedException(uuid);
        }
        matrixMapper.deleteMatrixByUuid(uuid);
        myDeleteMatrix(uuid);
    }

    protected abstract void myDeleteMatrix(String uuid);

    @Override
    public void copyMatrix(String sourceUuid, MatrixVo matrixVo){
        matrixVo.setFcu(UserContext.get().getUserUuid(true));
        matrixVo.setLcu(UserContext.get().getUserUuid(true));
        matrixMapper.insertMatrix(matrixVo);
        myCopyMatrix(sourceUuid, matrixVo);
    }

    protected abstract void myCopyMatrix(String sourceUuid, MatrixVo matrixVo);

    @Override
    public JSONObject importMatrix(MatrixVo matrixVo,MultipartFile multipartFile) throws IOException {
        return myImportMatrix(matrixVo, multipartFile);
    }

    protected abstract JSONObject myImportMatrix(MatrixVo matrixVo, MultipartFile multipartFile) throws IOException;

    @Override
    public HSSFWorkbook exportMatrix(MatrixVo matrixVo) {
        return myExportMatrix(matrixVo);
    }

    protected abstract HSSFWorkbook myExportMatrix(MatrixVo matrixVo);

    @Override
    public void saveAttributeList(String matrixUuid, List<MatrixAttributeVo> matrixAttributeList){
        mySaveAttributeList(matrixUuid, matrixAttributeList);
    }
    protected abstract void mySaveAttributeList(String matrixUuid, List<MatrixAttributeVo> matrixAttributeList);

    @Override
    public List<MatrixAttributeVo> getAttributeList(MatrixVo matrixVo) {
        return myGetAttributeList(matrixVo);
    }

    protected abstract List<MatrixAttributeVo> myGetAttributeList(MatrixVo matrixVo);

    @Override
    public JSONObject exportAttribute(MatrixVo matrixVo){
        return myExportAttribute(matrixVo);
    }
    protected abstract JSONObject myExportAttribute(MatrixVo matrixVo);

    @Override
    public JSONObject getTableData(MatrixDataVo dataVo) {
        return myGetTableData(dataVo);
    }

    protected abstract JSONObject myGetTableData(MatrixDataVo dataVo);

    @Override
    public JSONObject TableDataSearch(MatrixDataVo dataVo){
        return myTableDataSearch(dataVo);
    }
    protected abstract JSONObject myTableDataSearch(MatrixDataVo dataVo);
    @Override
    public List<Map<String, JSONObject>> TableColumnDataSearch(MatrixDataVo dataVo){
        return myTableColumnDataSearch(dataVo);
    }
    protected abstract List<Map<String, JSONObject>> myTableColumnDataSearch(MatrixDataVo dataVo);
    @Override
    public void saveTableRowData(String matrixUuid, JSONObject rowData) {
        mySaveTableRowData(matrixUuid, rowData);
    }

    protected abstract JSONObject mySaveTableRowData(String matrixUuid, JSONObject rowData);

    @Override
    public Map<String, String> getTableRowData(MatrixDataVo matrixDataVo) {
        return myGetTableRowData(matrixDataVo);
    }

    protected abstract Map<String, String> myGetTableRowData(MatrixDataVo matrixDataVo);

    @Override
    public void deleteTableRowData(String matrixUuid, List<String> uuidList) {
        myDeleteTableRowData(matrixUuid, uuidList);
    }
    protected abstract void myDeleteTableRowData(String matrixUuid, List<String> uuidList);
}
