package codedriver.framework.matrix.dto;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.dto.BasePageVo;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;

/**
 * @program: codedriver
 * @description:
 * @create: 2020-03-30 16:41
 **/
public class MatrixDataVo extends BasePageVo {
    private String matrixUuid;
    private List<String> columnList;
    private List<MatrixColumnVo> sourceColumnList;
    /**
     * 这个字段被super.defaultValue替代
     */
    private List<String> uuidList;
    private String uuidColumn;
    private String keywordColumn;
    private String keywordExpression;
    private String uuid;
    private String attrType;
    private List<String> attributeValueList;
//    private String attributeUuid;
    private JSONArray filterList;
    List<String> arrayColumnList;
    //表单编辑页引用配置项矩阵时的保存过滤条件数据
    private JSONArray attrFilterList;
    private JSONArray relFilterList;
//    private String targetColumn;
//
//    public String getTargetColumn() {
//        return targetColumn;
//    }
//
//    public void setTargetColumn(String targetColumn) {
//        this.targetColumn = targetColumn;
//    }

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

    /**
     * 这个方法被super.getDefaultValue()替代
     * @return
     */
    @Deprecated
	public List<String> getUuidList() {
		return uuidList;
	}

    /**
     * 这个方法被super.setDefaultValue()替代
     * @param uuidList
     */
    @Deprecated
	public void setUuidList(List<String> uuidList) {
		this.uuidList = uuidList;
	}

    public String getUuidColumn() {
        return uuidColumn;
    }

    public void setUuidColumn(String uuidColumn) {
        this.uuidColumn = uuidColumn;
    }

    public String getKeywordColumn() {
        return keywordColumn;
    }

    public void setKeywordColumn(String keywordColumn) {
        this.keywordColumn = keywordColumn;
    }

    public String getKeywordExpression() {
        return keywordExpression;
    }

    public void setKeywordExpression(String keywordExpression) {
        this.keywordExpression = keywordExpression;
    }

    public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

    public String getAttrType() {
        return attrType;
    }

    public void setAttrType(String attrType) {
        this.attrType = attrType;
    }

    public List<String> getAttributeValueList() {
        return attributeValueList;
    }

    public void setAttributeValueList(List<String> attributeValueList) {
        this.attributeValueList = attributeValueList;
    }

//    public String getAttributeUuid() {
//        return attributeUuid;
//    }
//
//    public void setAttributeUuid(String attributeUuid) {
//        this.attributeUuid = attributeUuid;
//    }

    public JSONArray getFilterList() {
        return filterList;
    }

    public void setFilterList(JSONArray filterList) {
        this.filterList = filterList;
    }

    public String getSchemaName() {
        return TenantContext.get().getDataDbName();
    }

    public List<String> getArrayColumnList() {
        return arrayColumnList;
    }

    public void setArrayColumnList(List<String> arrayColumnList) {
        this.arrayColumnList = arrayColumnList;
    }

    public JSONArray getAttrFilterList() {
        return attrFilterList;
    }

    public void setAttrFilterList(JSONArray attrFilterList) {
        this.attrFilterList = attrFilterList;
    }

    public JSONArray getRelFilterList() {
        return relFilterList;
    }

    public void setRelFilterList(JSONArray relFilterList) {
        this.relFilterList = relFilterList;
    }
}
