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

package neatlogic.framework.dao.mapper;

import neatlogic.framework.dto.schema.TableColumnVo;
import neatlogic.framework.dto.schema.TableVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SchemaMapper {
    List<TableVo> getTableInfo(TableVo table);

    List<TableColumnVo> getColumnInfoByTableName(TableVo table);

    List<Map<String, String>> testCiViewSql(String sql);

//    int checkTableHasData(String tableName);

//    int checkTableIsExists(@Param("schemaName") String schemaName, @Param("tableName") String tableName);

    int checkSchemaIsExists(String databaseName);

    String checkTableOrViewIsExists(@Param("tableSchema") String tableSchema, @Param("tableName") String tableName);

    List<String> getTableOrViewAllColumnNameList(@Param("tableSchema") String tableSchema, @Param("tableName") String tableName);

//    void insertAttrToCiTable(@Param("tableName") String tableName, @Param("attrVo") MatrixViewAttributeVo attrVo);

//    void insertCiTable(@Param("tableName") String tableName);

    void insertView(String sql);

//    void deleteAttrFromCiTable(@Param("tableName") String tableName, @Param("attrVo") MatrixViewAttributeVo attrVo);

    void deleteTable(String tableName);

    void deleteView(String tableName);

}
