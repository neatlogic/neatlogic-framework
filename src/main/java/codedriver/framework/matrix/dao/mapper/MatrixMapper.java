package codedriver.framework.matrix.dao.mapper;

import java.util.List;

import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.matrix.dto.MatrixVo;
import codedriver.framework.matrix.dto.ProcessMatrixFormComponentVo;

public interface MatrixMapper {
    public int insertMatrix(MatrixVo matrixVo);

    public int searchMatrixCount(MatrixVo matrixVo);

    public MatrixVo getMatrixByUuid(String uuid);

    public ValueTextVo getMatrixByUuidForSelect(String uuid);

    public List<MatrixVo> searchMatrix(MatrixVo matrixVo);

    public List<ValueTextVo> searchMatrixForSelect(MatrixVo matrixVo);

	public int checkMatrixIsExists(String uuid);

	public int checkMatrixNameIsRepeat(MatrixVo matrixVo);
	
	public List<ProcessMatrixFormComponentVo> getMatrixFormComponentByMatrixUuid(String matrixUuid);

    public int deleteMatrixByUuid(String uuid);

    public int updateMatrixNameAndLcu(MatrixVo matrixVo);
}
