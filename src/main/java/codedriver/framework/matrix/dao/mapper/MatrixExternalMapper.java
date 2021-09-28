package codedriver.framework.matrix.dao.mapper;

import codedriver.framework.matrix.dto.MatrixExternalVo;

public interface MatrixExternalMapper {

    MatrixExternalVo getMatrixExternalByMatrixUuid(String matrixUuid);

    int replaceMatrixExternal(MatrixExternalVo matrixExternalVo);

    int deleteMatrixExternalByMatrixUuid(String matrixUuid);
}
