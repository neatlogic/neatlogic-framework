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

    void replaceIntoField(@Param("fieldVo") FullTextIndexFieldWordVo fieldVo, @Param("moduleId") String moduleId);

    void insertFieldOffset(@Param("offsetVo") FullTextIndexOffsetVo offsetVo, @Param("moduleId") String moduleId);

    void insertTarget(FullTextIndexTargetVo fullTextIndexTargetVo);

    void insertContent(@Param("contentVo") FullTextIndexContentVo contentVo, @Param("moduleId") String moduleId);

    void deleteFullTextIndexByType(FullTextIndexTypeVo fullTextIndexTypeVo);

    void deleteFullTextIndexByTargetIdAndType(@Param("fullTextIndexVo") FullTextIndexVo fullTextIndexVo, @Param("moduleId") String moduleId);
}
