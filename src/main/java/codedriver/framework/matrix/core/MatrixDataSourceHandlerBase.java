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
import codedriver.framework.matrix.exception.MatrixAttributeNotFoundException;
import codedriver.framework.matrix.exception.MatrixNotFoundException;
import codedriver.framework.matrix.exception.MatrixReferencedCannotBeDeletedException;
import codedriver.module.framework.matrix.service.MatrixService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author linbq
 * @since 2021/11/5 10:26
 **/
public abstract class MatrixDataSourceHandlerBase implements IMatrixDataSourceHandler {
    /**
     * 下拉列表value和text列的组合连接符
     **/
    protected final static String SELECT_COMPOSE_JOINER = "&=&";

    protected static MatrixMapper matrixMapper;

    @Resource
    public void setMatrixMapper(MatrixMapper _matrixMapper) {
        matrixMapper = _matrixMapper;
    }

    @Override
    public void saveMatrix(MatrixVo matrixVo) throws Exception {
        if (matrixMapper.checkMatrixIsExists(matrixVo.getUuid()) == 0) {
            matrixMapper.insertMatrix(matrixVo);
            mySaveMatrix(matrixVo);
        } else {
            if (mySaveMatrix(matrixVo)) {
                matrixMapper.updateMatrixNameAndLcu(matrixVo);
            }
        }
    }

    protected abstract boolean mySaveMatrix(MatrixVo matrixVo) throws Exception;

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



    protected JSONArray getTheadList(List<MatrixAttributeVo> attributeList) {
        JSONArray theadList = new JSONArray();
        JSONObject selectionObj = new JSONObject();
        selectionObj.put("key", "selection");
        selectionObj.put("width", 60);
        theadList.add(selectionObj);
        for (MatrixAttributeVo attributeVo : attributeList) {
            JSONObject columnObj = new JSONObject();
            columnObj.put("title", attributeVo.getName());
            columnObj.put("key", attributeVo.getUuid());
            theadList.add(columnObj);
        }
        JSONObject actionObj = new JSONObject();
        actionObj.put("title", "");
        actionObj.put("key", "action");
        actionObj.put("align", "right");
        actionObj.put("width", 10);
        theadList.add(actionObj);
        return theadList;
    }

    protected JSONArray getTheadList(String matrixUuid, List<MatrixAttributeVo> attributeList, List<String> columnList) {
        Map<String, MatrixAttributeVo> attributeMap = new HashMap<>();
        for (MatrixAttributeVo attribute : attributeList) {
            attributeMap.put(attribute.getUuid(), attribute);
        }
        JSONArray theadList = new JSONArray();
        for (String column : columnList) {
            MatrixAttributeVo attribute = attributeMap.get(column);
            if (attribute == null) {
                throw new MatrixAttributeNotFoundException(matrixUuid, column);
            }
            JSONObject theadObj = new JSONObject();
            theadObj.put("key", attribute.getUuid());
            theadObj.put("title", attribute.getName());
            theadList.add(theadObj);
        }
        return theadList;
    }
}