package codedriver.framework.matrix.dto;

import codedriver.framework.common.dto.BasePageVo;

import java.util.List;

/**
 * @program: codedriver
 * @description:
 * @create: 2020-03-30 16:41
 **/
public class MatrixDataVo extends BasePageVo {
    private String keyword;
    private String matrixUuid;
    private List<String> columnList;
    private List<MatrixColumnVo> sourceColumnList;
    private List<String> uuidList;
    private String uuid;
//    private String targetColumn;
//
//    public String getTargetColumn() {
//        return targetColumn;
//    }
//
//    public void setTargetColumn(String targetColumn) {
//        this.targetColumn = targetColumn;
//    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getMatrixUuid() {
        return matrixUuid;
    }

    public void setMatrixUuid(String matrixUuid) {
        this.matrixUuid = matrixUuid;
    }

    public List<String> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<String> columnList) {
        this.columnList = columnList;
    }

    public List<MatrixColumnVo> getSourceColumnList() {
        return sourceColumnList;
    }

    public void setSourceColumnList(List<MatrixColumnVo> sourceColumnList) {
        this.sourceColumnList = sourceColumnList;
    }

	public List<String> getUuidList() {
		return uuidList;
	}

	public void setUuidList(List<String> uuidList) {
		this.uuidList = uuidList;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
