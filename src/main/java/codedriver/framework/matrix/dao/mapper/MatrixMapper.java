package codedriver.framework.matrix.dao.mapper;

import java.util.List;

import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.matrix.dto.MatrixCiVo;
import codedriver.framework.matrix.dto.MatrixExternalVo;
import codedriver.framework.matrix.dto.MatrixViewVo;
import codedriver.framework.matrix.dto.MatrixVo;

public interface MatrixMapper {

    MatrixVo getMatrixByUuid(String uuid);

    int checkMatrixIsExists(String uuid);

    int checkMatrixNameIsRepeat(MatrixVo matrixVo);

    int checkMatrixLabelIsRepeat(MatrixVo matrixVo);

    List<MatrixVo> getMatrixListByUuidList(List<String> uuidList);

    int searchMatrixCount(MatrixVo matrixVo);

    List<MatrixVo> searchMatrix(MatrixVo matrixVo);

    MatrixExternalVo getMatrixExternalByMatrixUuid(String matrixUuid);

    MatrixViewVo getMatrixViewByMatrixUuid(String matrixUuid);

    MatrixCiVo getMatrixCiByMatrixUuid(String matrixUuid);

    int insertMatrix(MatrixVo matrixVo);

    int replaceMatrixExternal(MatrixExternalVo matrixExternalVo);

    int replaceMatrixView(MatrixViewVo matrixViewVo);

    int replaceMatrixCi(MatrixCiVo matrixCiVo);

    int updateMatrixNameAndLcu(MatrixVo matrixVo);

    int deleteMatrixByUuid(String uuid);

    int deleteMatrixExternalByMatrixUuid(String matrixUuid);

    int deleteMatrixViewByMatrixUuid(String matrixUuid);

    int deleteMatrixCiByMatrixUuid(String matrixUuid);
}
