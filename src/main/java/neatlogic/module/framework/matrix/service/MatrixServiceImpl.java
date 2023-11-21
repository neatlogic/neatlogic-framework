package neatlogic.module.framework.matrix.service;

import neatlogic.framework.matrix.core.MatrixPrivateDataSourceHandlerFactory;
import neatlogic.framework.matrix.dao.mapper.MatrixMapper;
import neatlogic.framework.matrix.dto.MatrixVo;
import neatlogic.framework.matrix.exception.MatrixNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MatrixServiceImpl implements MatrixService {

    @Resource
    private MatrixMapper matrixMapper;

    @Override
    public MatrixVo getMatrixByUuid(String uuid) {
        MatrixVo matrixVo = matrixMapper.getMatrixByUuid(uuid);
        if (matrixVo != null) {
            return matrixVo;
        }
        matrixVo = MatrixPrivateDataSourceHandlerFactory.getMatrixVo(uuid);
        if (matrixVo != null) {
            return matrixVo;
        }
        throw new MatrixNotFoundException(uuid);
    }
}
