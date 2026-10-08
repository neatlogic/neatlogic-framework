/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.dto.healthcheck;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.healthcheck.enums.SchemaType;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.$;

import java.text.DecimalFormat;
import java.util.List;

/**
 * 数据库表数据
 */
public class DatabaseFragmentVo extends BasePageVo {
    @EntityField(name = "framework.healthcheck.databasefragment.schema", type = ApiParamType.STRING)
    private String schema;
    @EntityField(name = "framework.healthcheck.databasefragment.schematype", type = ApiParamType.ENUM, member = SchemaType.class)
    private String schemaType = SchemaType.MAIN.getValue();
    @EntityField(name = "framework.healthcheck.databasefragment.name", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "framework.healthcheck.databasefragment.engine", type = ApiParamType.STRING)
    private String engine;
    @EntityField(name = "framework.healthcheck.databasefragment.datarows", type = ApiParamType.INTEGER)
    private int dataRows;
    @EntityField(name = "framework.healthcheck.databasefragment.datasize", type = ApiParamType.INTEGER)
    private int dataSize;
    @EntityField(name = "framework.healthcheck.databasefragment.datasizetext", type = ApiParamType.STRING)
    private String dataSizeText;
    @EntityField(name = "framework.healthcheck.databasefragment.indexsize", type = ApiParamType.INTEGER)
    private int indexSize;
    @EntityField(name = "framework.healthcheck.databasefragment.indexsizetext", type = ApiParamType.STRING)
    private String indexSizeText;
    @EntityField(name = "framework.healthcheck.databasefragment.totalsize", type = ApiParamType.INTEGER)
    private int totalSize;
    @EntityField(name = "framework.healthcheck.databasefragment.totalsizetext", type = ApiParamType.STRING)
    private String totalSizeText;
    @EntityField(name = "framework.healthcheck.databasefragment.datafree", type = ApiParamType.INTEGER)
    private int dataFree;
    @EntityField(name = "framework.healthcheck.databasefragment.datafreetext", type = ApiParamType.STRING)
    private String dataFreeText;
    @EntityField(name = "framework.healthcheck.databasefragment.fragmentrate", type = ApiParamType.INTEGER)
    private float fragmentRate;
    @EntityField(name = "framework.healthcheck.databasefragment.sortlist", type = ApiParamType.JSONARRAY)
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

    private static final String[] SIZE_UNITS = new String[]{"", "K", "M", "G"};
    private final DecimalFormat decimalFormat = new DecimalFormat("0.##");

    /** 返回按当前语言显示的数据文件大小。 */
    public String getDataSizeText() {
        float d = dataSize;
        int unitindex = 0;
        while (d > 1024 && unitindex < SIZE_UNITS.length - 1) {
            d = d / 1024;
            unitindex += 1;
        }
        String unit = unitindex == 0 ? $.t("framework.healthcheck.databasefragment.byte") : SIZE_UNITS[unitindex];
        return decimalFormat.format(d) + unit;
    }

    /** 返回按当前语言显示的索引文件大小。 */
    public String getIndexSizeText() {
        float d = indexSize;
        int unitindex = 0;
        while (d > 1024 && unitindex < SIZE_UNITS.length - 1) {
            d = d / 1024;
            unitindex += 1;
        }
        String unit = unitindex == 0 ? $.t("framework.healthcheck.databasefragment.byte") : SIZE_UNITS[unitindex];
        return decimalFormat.format(d) + unit;
    }

    /** 返回按当前语言显示的总占用空间。 */
    public String getTotalSizeText() {
        float d = totalSize;
        int unitindex = 0;
        while (d > 1024 && unitindex < SIZE_UNITS.length - 1) {
            d = d / 1024;
            unitindex += 1;
        }
        String unit = unitindex == 0 ? $.t("framework.healthcheck.databasefragment.byte") : SIZE_UNITS[unitindex];
        return decimalFormat.format(d) + unit;
    }

    /** 返回按当前语言显示的数据空闲空间。 */
    public String getDataFreeText() {
        float d = dataFree;
        int unitindex = 0;
        while (d > 1024 && unitindex < SIZE_UNITS.length - 1) {
            d = d / 1024;
            unitindex += 1;
        }
        String unit = unitindex == 0 ? $.t("framework.healthcheck.databasefragment.byte") : SIZE_UNITS[unitindex];
        return decimalFormat.format(d) + unit;
    }

}
