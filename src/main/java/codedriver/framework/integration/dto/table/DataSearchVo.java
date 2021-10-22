/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.integration.dto.table;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.matrix.dto.MatrixColumnVo;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

/**
 * @program: codedriver
 * @description:
 * @create: 2020-03-30 16:41
 **/
public class DataSearchVo extends BasePageVo {
    private String integrationUuid;
    private List<String> columnList;
    private List<MatrixColumnVo> sourceColumnList;
//    private List<String> uuidList;
    private String uuid;
    private String attrType;
    private List<String> attributeValueList;
    private String attributeUuid;
    private JSONArray filterList;
    private String schemaName = TenantContext.get().getDataDbName();

    public String getIntegrationUuid() {
        return integrationUuid;
    }

    public void setIntegrationUuid(String integrationUuid) {
        this.integrationUuid = integrationUuid;
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

//	public List<String> getUuidList() {
//		return uuidList;
//	}
//
//	public void setUuidList(List<String> uuidList) {
//		this.uuidList = uuidList;
//	}

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

    public String getAttributeUuid() {
        return attributeUuid;
    }

    public void setAttributeUuid(String attributeUuid) {
        this.attributeUuid = attributeUuid;
    }

    public JSONArray getFilterList() {
        return filterList;
    }

    public void setFilterList(JSONArray filterList) {
        this.filterList = filterList;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }
}
