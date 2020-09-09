package codedriver.framework.matrix.dao.mapper;

import codedriver.framework.matrix.dto.MatrixExternalVo;

public interface MatrixExternalMapper {
    public void insertMatrixExternal(MatrixExternalVo matrixExternalVo);

    public void updateMatrixExternal(MatrixExternalVo externalVo);

    public void deleteMatrixExternalByMatrixUuid(String matrixUuid);

    public MatrixExternalVo getMatrixExternalByMatrixUuid(String matrixUuid);

	public int getMatrixExternalIsExists(String matrixUuid);
}
