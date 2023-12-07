package neatlogic.module.framework.importexport.handler;

import neatlogic.framework.importexport.constvalue.FrameworkImportExportHandlerType;
import neatlogic.framework.importexport.core.ImportExportHandlerBase;
import neatlogic.framework.importexport.core.ImportExportHandlerType;
import neatlogic.framework.importexport.dto.ImportExportBaseInfoVo;
import neatlogic.framework.importexport.dto.ImportExportPrimaryChangeVo;
import neatlogic.framework.importexport.dto.ImportExportVo;
import neatlogic.framework.matrix.core.IMatrixDataSourceHandler;
import neatlogic.framework.matrix.core.MatrixDataSourceHandlerFactory;
import neatlogic.framework.matrix.dao.mapper.MatrixMapper;
import neatlogic.framework.matrix.dto.MatrixVo;
import neatlogic.framework.matrix.exception.MatrixDataSourceHandlerNotFoundException;
import neatlogic.framework.matrix.exception.MatrixNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipOutputStream;

@Component
public class MatrixImportExportHandler extends ImportExportHandlerBase {

    @Resource
    private MatrixMapper matrixMapper;

    @Override
    public ImportExportHandlerType getType() {
        return FrameworkImportExportHandlerType.MATRIX;
    }

    @Override
    public boolean checkImportAuth(ImportExportVo importExportVo) {
        return true;
    }

    @Override
    public boolean checkExportAuth(Object primaryKey) {
        return true;
    }

    @Override
    public boolean checkIsExists(ImportExportBaseInfoVo importExportBaseInfoVo) {
        return matrixMapper.getMatrixByLabel(importExportBaseInfoVo.getName()) != null;
    }

    @Override
    public Object getPrimaryByName(ImportExportVo importExportVo) {
        MatrixVo matrix = matrixMapper.getMatrixByLabel(importExportVo.getName());
        if (matrix == null) {
            throw new MatrixNotFoundException(importExportVo.getName());
        }
        return matrix.getUuid();
    }

    @Override
    public Object importData(ImportExportVo importExportVo, List<ImportExportPrimaryChangeVo> primaryChangeList) {
        MatrixVo matrix = importExportVo.getData().toJavaObject(MatrixVo.class);
        MatrixVo oldMatrix = matrixMapper.getMatrixByLabel(importExportVo.getName());
        if (oldMatrix != null) {
            matrix.setUuid(oldMatrix.getUuid());
        } else {
            if (matrixMapper.getMatrixByUuid(matrix.getUuid()) != null) {
                matrix.setUuid(null);
            }
        }
        IMatrixDataSourceHandler matrixDataSourceHandler = MatrixDataSourceHandlerFactory.getHandler(matrix.getType());
        if (matrixDataSourceHandler == null) {
            throw new MatrixDataSourceHandlerNotFoundException(matrix.getType());
        }
        if (Objects.equals(matrix.getType(), "external")) {
            Object newPrimaryKey = getNewPrimaryKey(FrameworkImportExportHandlerType.INTEGRATION, matrix.getIntegrationUuid(), primaryChangeList);
            if (newPrimaryKey != null) {
                matrix.setIntegrationUuid((String) newPrimaryKey);
            }
        } else if (Objects.equals(matrix.getType(), "cmdbci")) {
            Object newPrimaryKey = getNewPrimaryKey(FrameworkImportExportHandlerType.CMDB_CI, matrix.getId(), primaryChangeList);
            if (newPrimaryKey != null) {
                matrix.setCiId((Long) newPrimaryKey);
            }
        }
        matrixDataSourceHandler.importMatrix(matrix);
        return matrix.getUuid();
    }

    @Override
    protected ImportExportVo myExportData(Object primaryKey, List<ImportExportBaseInfoVo> dependencyList, ZipOutputStream zipOutputStream) {
        String uuid = (String) primaryKey;
        MatrixVo matrix = matrixMapper.getMatrixByUuid(uuid);
        if (matrix == null) {
            throw new MatrixNotFoundException(uuid);
        }
        IMatrixDataSourceHandler matrixDataSourceHandler = MatrixDataSourceHandlerFactory.getHandler(matrix.getType());
        if (matrixDataSourceHandler == null) {
            throw new MatrixDataSourceHandlerNotFoundException(matrix.getType());
        }
        MatrixVo matrixVo = matrixDataSourceHandler.exportMatrix(matrix);
        if (Objects.equals(matrix.getType(), "external")) {
            doExportData(FrameworkImportExportHandlerType.INTEGRATION, matrixVo.getIntegrationUuid(), dependencyList, zipOutputStream);
        } else if (Objects.equals(matrix.getType(), "cmdbci")) {
            doExportData(FrameworkImportExportHandlerType.CMDB_CI, matrixVo.getId(), dependencyList, zipOutputStream);
        }
        ImportExportVo importExportVo = new ImportExportVo(this.getType().getValue(), primaryKey, matrixVo.getLabel());
        importExportVo.setDataWithObject(matrixVo);
        return importExportVo;
    }
}
