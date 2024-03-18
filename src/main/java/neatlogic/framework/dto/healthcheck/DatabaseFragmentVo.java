/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.dto.healthcheck;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.healthcheck.enums.SchemaType;
import neatlogic.framework.restful.annotation.EntityField;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
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
    @EntityField(name = "数据文件占用空间，单位：字节", type = ApiParamType.INTEGER)
    private int dataSize;
    @EntityField(name = "数据文件占用空间（带单位）", type = ApiParamType.STRING)
    private String dataSizeText;
    @EntityField(name = "索引文件占用空间，单位：字节", type = ApiParamType.INTEGER)
    private int indexSize;
    @EntityField(name = "索引文件占用空间（带单位）", type = ApiParamType.STRING)
    private String indexSizeText;
    @EntityField(name = "总使用占用空间，单位：字节", type = ApiParamType.INTEGER)
    private int totalSize;
    @EntityField(name = "总使用占用空间（带单位）", type = ApiParamType.STRING)
    private String totalSizeText;
    @EntityField(name = "数据空闲空间，单位：字节", type = ApiParamType.INTEGER)
    private int dataFree;
    @EntityField(name = "数据空闲空间（带单位）", type = ApiParamType.STRING)
    private String dataFreeText;
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

    String[] units = new String[]{"字节", "K", "M", "G"};

    public String getDataSizeText() {
        if (StringUtils.isBlank(dataSizeText)) {
            float d = dataSize;
            int unitindex = 0;
            while (d > 1024 && unitindex <= 3) {
                d = d / 1024;
                unitindex += 1;
            }
            dataSizeText = decimalFormat.format(d) + units[unitindex];
        }
        return dataSizeText;
    }


    public String getIndexSizeText() {
        if (StringUtils.isBlank(indexSizeText)) {
            float d = indexSize;
            int unitindex = 0;
            while (d > 1024 && unitindex <= 3) {
                d = d / 1024;
                unitindex += 1;
            }
            indexSizeText = decimalFormat.format(d) + units[unitindex];
        }
        return indexSizeText;
    }


    public String getTotalSizeText() {
        if (StringUtils.isBlank(totalSizeText)) {
            float d = totalSize;
            int unitindex = 0;
            while (d > 1024 && unitindex <= 3) {
                d = d / 1024;
                unitindex += 1;
            }
            totalSizeText = decimalFormat.format(d) + units[unitindex];
        }
        return totalSizeText;
    }

    DecimalFormat decimalFormat = new DecimalFormat("0.##");

    public String getDataFreeText() {
        if (StringUtils.isBlank(dataFreeText)) {
            float d = dataFree;
            int unitindex = 0;
            while (d > 1024 && unitindex <= 3) {
                d = d / 1024;
                unitindex += 1;
            }
            dataFreeText = decimalFormat.format(d) + units[unitindex];
        }
        return dataFreeText;
    }

}
