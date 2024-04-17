package neatlogic.framework.matrix.dto;

import com.alibaba.fastjson.JSONArray;
import neatlogic.framework.common.dto.BasePageVo;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: neatlogic
 * @description:
 * @create: 2020-03-30 16:41
 **/
public class MatrixDataVo extends BasePageVo {
    private String matrixUuid;

    private String matrixLabel;

    private List<String> columnList;

    private List<String> columnNameList;

    @Deprecated
    private List<MatrixColumnVo> sourceColumnList;
    /**
     * 这个字段被super.defaultValue替代
     */
    private List<String> uuidList;
    private String uuidColumn;
    private String keywordColumn;
    private String keywordColumnName;
    private String keywordExpression;
    private String uuid;
    private String attrType;
    private List<String> attributeValueList;
    private List<MatrixFilterVo> filterList;
    List<String> arrayColumnList;
    //表单编辑页引用配置项矩阵时的保存过滤条件数据
    @Deprecated
    private JSONArray attrFilterList;
    @Deprecated
    private JSONArray relFilterList;
    @Deprecated
    private Long filterCiEntityId;
    @Deprecated
    private Long filterCiId;

    /**
     * 用于下拉框默认值回显
     */
    private List<MatrixDefaultValueFilterVo> defaultValueFilterList;

    /**
     * 查询数据时不能为空的column列表
     */
    private List<String> notNullColumnList;
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
    @Deprecated
    public List<MatrixColumnVo> getSourceColumnList() {
        if (sourceColumnList == null) {
            sourceColumnList = new ArrayList<>();
        }
        return sourceColumnList;
    }
    @Deprecated
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


    public List<MatrixFilterVo> getFilterList() {
        if (filterList == null) {
            filterList = new ArrayList<>();
        }
        return filterList;
    }

    public void setFilterList(List<MatrixFilterVo> filterList) {
        this.filterList = filterList;
    }

    public List<String> getArrayColumnList() {
        return arrayColumnList;
    }

    public void setArrayColumnList(List<String> arrayColumnList) {
        this.arrayColumnList = arrayColumnList;
    }
    @Deprecated
    public JSONArray getAttrFilterList() {
        return attrFilterList;
    }
    @Deprecated
    public void setAttrFilterList(JSONArray attrFilterList) {
        this.attrFilterList = attrFilterList;
    }
    @Deprecated
    public JSONArray getRelFilterList() {
        return relFilterList;
    }
    @Deprecated
    public void setRelFilterList(JSONArray relFilterList) {
        this.relFilterList = relFilterList;
    }
    @Deprecated
    public Long getFilterCiEntityId() {
        return filterCiEntityId;
    }
    @Deprecated
    public void setFilterCiEntityId(Long filterCiEntityId) {
        this.filterCiEntityId = filterCiEntityId;
    }
    @Deprecated
    public Long getFilterCiId() {
        return filterCiId;
    }
    @Deprecated
    public void setFilterCiId(Long filterCiId) {
        this.filterCiId = filterCiId;
    }

    public List<MatrixDefaultValueFilterVo> getDefaultValueFilterList() {
        return defaultValueFilterList;
    }

    public void setDefaultValueFilterList(List<MatrixDefaultValueFilterVo> defaultValueFilterList) {
        this.defaultValueFilterList = defaultValueFilterList;
    }

    public List<String> getNotNullColumnList() {
        return notNullColumnList;
    }

    public void setNotNullColumnList(List<String> notNullColumnList) {
        this.notNullColumnList = notNullColumnList;
    }

    public String getMatrixLabel() {
        return matrixLabel;
    }

    public void setMatrixLabel(String matrixLabel) {
        this.matrixLabel = matrixLabel;
    }

    public List<String> getColumnNameList() {
        return columnNameList;
    }

    public void setColumnNameList(List<String> columnNameList) {
        this.columnNameList = columnNameList;
    }

    public String getKeywordColumnName() {
        return keywordColumnName;
    }

    public void setKeywordColumnName(String keywordColumnName) {
        this.keywordColumnName = keywordColumnName;
    }
}
