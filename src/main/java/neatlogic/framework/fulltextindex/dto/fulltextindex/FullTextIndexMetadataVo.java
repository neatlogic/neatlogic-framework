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

package neatlogic.framework.fulltextindex.dto.fulltextindex;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;

import java.util.ArrayList;
import java.util.List;

public class FullTextIndexMetadataVo {
    @EntityField(name = "处理器", type = ApiParamType.STRING)
    private String handler;
    @EntityField(name = "类型", type = ApiParamType.STRING)
    private String type;
    @EntityField(name = "类型名称", type = ApiParamType.STRING)
    private String typeName;
    @EntityField(name = "索引名称", type = ApiParamType.STRING)
    private String indexName;
    @EntityField(name = "索引是否存在", type = ApiParamType.BOOLEAN)
    private boolean indexExists;
    @EntityField(name = "索引数量", type = ApiParamType.INTEGER)
    private Integer indexCount;
    @EntityField(name = "字段列表", type = ApiParamType.JSONARRAY)
    private List<FullTextIndexFieldMetaVo> fieldList = new ArrayList<>();

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public boolean isIndexExists() {
        return indexExists;
    }

    public void setIndexExists(boolean indexExists) {
        this.indexExists = indexExists;
    }

    public Integer getIndexCount() {
        return indexCount;
    }

    public void setIndexCount(Integer indexCount) {
        this.indexCount = indexCount;
    }

    public List<FullTextIndexFieldMetaVo> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<FullTextIndexFieldMetaVo> fieldList) {
        this.fieldList = fieldList;
    }
}
