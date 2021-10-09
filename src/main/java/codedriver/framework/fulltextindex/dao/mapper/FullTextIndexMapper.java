/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.fulltextindex.dao.mapper;

import codedriver.framework.fulltextindex.dto.fulltextindex.FullTextIndexContentVo;
import codedriver.framework.fulltextindex.dto.fulltextindex.FullTextIndexFieldWordVo;
import codedriver.framework.fulltextindex.dto.fulltextindex.FullTextIndexOffsetVo;
import codedriver.framework.fulltextindex.dto.fulltextindex.FullTextIndexVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FullTextIndexMapper {
    List<FullTextIndexVo> getFullTextIndexListByKeywordListAndTargetList(@Param("keywordList") List<String> keywordList, @Param("targetIdList") List<Long> targetIdList, @Param("moduleId") String moduleId);

    FullTextIndexContentVo getContentByTargetId(@Param("targetId") Long targetId, @Param("moduleId") String moduleId);

    List<FullTextIndexContentVo> getContentByTargetIdList(@Param("targetIdList") List<Long> targetIdList, @Param("moduleId") String moduleId);

    void insertField(@Param("fieldVo") FullTextIndexFieldWordVo fieldVo, @Param("moduleId") String moduleId);

    void insertFieldOffset(@Param("offsetVo") FullTextIndexOffsetVo offsetVo, @Param("moduleId") String moduleId);

    void insertContent(@Param("contentVo") FullTextIndexContentVo contentVo, @Param("moduleId") String moduleId);

    void deleteFullTextIndexByTargetIdAndType(@Param("fullTextIndexVo") FullTextIndexVo fullTextIndexVo, @Param("moduleId") String moduleId);
}
