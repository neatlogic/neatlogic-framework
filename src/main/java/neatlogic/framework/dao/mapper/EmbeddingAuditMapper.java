/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the Sustainable Use License (SUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.dao.mapper;

import neatlogic.framework.dto.embedding.EmbeddingAuditVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EmbeddingAuditMapper {
    void insertEmbeddingAudit(@Param("targetId") Long targetId, @Param("targetType") String targetType);

    void deleteEmbeddingAudit(@Param("targetId") Long targetId, @Param("targetType") String targetType);

    List<EmbeddingAuditVo> searchEmbeddingAudit(EmbeddingAuditVo embeddingAuditVo);
}
