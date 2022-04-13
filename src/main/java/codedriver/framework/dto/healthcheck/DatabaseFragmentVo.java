/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dto.healthcheck;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.healthcheck.enums.SchemaType;
import codedriver.framework.restful.annotation.EntityField;

import java.util.List;

/**
 * 数据库表数据
 */
public class DatabaseFragmentVo extends BasePageVo {
    @EntityField(name = "schema名称", type = ApiParamType.STRING)
    private String schema;
    @EntityField(name = "schema类型", type = ApiParamType.ENUM, member = SchemaType.class)
    private String schemaType = SchemaType.MAIN.getValue();
    @EntityField(name = "表名", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "引擎", type = ApiParamType.STRING)
    private String engine;
    @EntityField(name = "行数", type = ApiParamType.INTEGER)
    private int dataRows;
    @EntityField(name = "数据文件占用空间，单位：M", type = ApiParamType.INTEGER)
    private int dataSize;
    @EntityField(name = "索引文件占用空间，单位：M", type = ApiParamType.INTEGER)
    private int indexSize;
    @EntityField(name = "总使用占用空间，单位：M", type = ApiParamType.INTEGER)
    private int totalSize;
    @EntityField(name = "数据空闲空间，单位：M", type = ApiParamType.INTEGER)
    private int dataFree;
    @EntityField(name = "碎片率", type = ApiParamType.INTEGER)
    private float fragmentRate;
    @EntityField(name = "排序", type = ApiParamType.JSONARRAY)
    private List<String> sortList;

    public String getSchema() {
        if (this.schemaType.equals(SchemaType.MAIN.getValue())) {
            schema = TenantContext.get().getDbName();
        } else if (this.schemaType.equals(SchemaType.DATA.getValue())) {
            schema = TenantContext.get().getDataDbName();
        }
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getSchemaType() {
        return schemaType;
    }

    public void setSchemaType(String schemaType) {
        this.schemaType = schemaType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public int getDataRows() {
        return dataRows;
    }

    public void setDataRows(int dataRows) {
        this.dataRows = dataRows;
    }

    public int getTotalSize() {
        return this.dataSize + this.indexSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public int getDataSize() {
        return dataSize;
    }

    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }

    public int getIndexSize() {
        return indexSize;
    }

    public void setIndexSize(int indexSize) {
        this.indexSize = indexSize;
    }

    public int getDataFree() {
        return dataFree;
    }

    public void setDataFree(int dataFree) {
        this.dataFree = dataFree;
    }

    public float getFragmentRate() {
        return fragmentRate;
    }

    public void setFragmentRate(float fragmentRate) {
        this.fragmentRate = fragmentRate;
    }

    public List<String> getSortList() {
        return sortList;
    }

    public void setSortList(List<String> sortList) {
        this.sortList = sortList;
    }
}
