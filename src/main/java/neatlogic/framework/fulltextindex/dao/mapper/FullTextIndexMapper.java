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

package neatlogic.framework.fulltextindex.dao.mapper;

import neatlogic.framework.fulltextindex.dto.fulltextindex.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FullTextIndexMapper {
    int getFullTextIndexCountByType(FullTextIndexTypeVo fullTextIndexTypeVo);

    List<FullTextIndexVo> getFullTextIndexListByKeywordListAndTargetList(@Param("keywordList") List<String> keywordList, @Param("targetIdList") List<Long> targetIdList, @Param("moduleId") String moduleId);

    FullTextIndexContentVo getContentByTargetId(@Param("targetId") Long targetId, @Param("moduleId") String moduleId);

    List<FullTextIndexContentVo> getContentByTargetIdList(@Param("targetIdList") List<Long> targetIdList, @Param("moduleId") String moduleId);

    void updateTargetError(FullTextIndexTargetVo fullTextIndexTargetVo);


    void insertIndexField(@Param("fieldVo") FullTextIndexFieldWordVo fieldVo, @Param("moduleId") String moduleId);

    void updateIndexField(@Param("fieldVo") FullTextIndexFieldWordVo fieldVo, @Param("moduleId") String moduleId);

    FullTextIndexFieldWordVo getFulltextIndexField(@Param("fieldVo") FullTextIndexFieldWordVo fieldVo, @Param("moduleId") String moduleId);

    void insertFieldOffset(@Param("offsetVo") FullTextIndexOffsetVo offsetVo, @Param("moduleId") String moduleId);

    void insertTarget(FullTextIndexTargetVo fullTextIndexTargetVo);

    void insertContent(@Param("contentVo") FullTextIndexContentVo contentVo, @Param("moduleId") String moduleId);

    void deleteFullTextIndexByType(FullTextIndexTypeVo fullTextIndexTypeVo);

    void clearEmptyFullTextField(FullTextIndexTypeVo fullTextIndexTypeVo);

    void clearErrorFullTextTarget(FullTextIndexTypeVo fullTextIndexTypeVo);

    void deleteFullTextIndexByTargetIdAndType(@Param("fullTextIndexVo") FullTextIndexVo fullTextIndexVo, @Param("moduleId") String moduleId);
}
