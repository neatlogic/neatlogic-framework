package codedriver.framework.matrix.dao.mapper;

import java.util.List;

import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.matrix.dto.MatrixVo;

public interface MatrixMapper {
    int insertMatrix(MatrixVo matrixVo);

    int searchMatrixCount(MatrixVo matrixVo);

    MatrixVo getMatrixByUuid(String uuid);

    List<MatrixVo> getMatrixListByUuidList(List<String> uuidList);

    List<MatrixVo> searchMatrix(MatrixVo matrixVo);

    int checkMatrixIsExists(String uuid);

    int checkMatrixNameIsRepeat(MatrixVo matrixVo);

    int checkMatrixLabelIsRepeat(MatrixVo matrixVo);

    int deleteMatrixByUuid(String uuid);

    int updateMatrixNameAndLcu(MatrixVo matrixVo);
}
