package codedriver.framework.fulltextindex.dao.mapper;

import codedriver.framework.fulltextindex.dto.FullTextIndexContentVo;
import codedriver.framework.fulltextindex.dto.FullTextIndexFieldWordVo;
import codedriver.framework.fulltextindex.dto.FullTextIndexOffsetVo;
import codedriver.framework.fulltextindex.dto.FullTextIndexVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FullTextIndexMapper {
    List<FullTextIndexVo> getFullTextIndexListByKeywordListAndTargetList(@Param("keywordList")List<String> keywordList, @Param("targetIdList") List<Long> targetIdList);

    FullTextIndexContentVo getContentByTargetId(Long targetId);

    List<FullTextIndexContentVo> getContentByTargetIdList(List<Long> targetIdList);

    void insertField(FullTextIndexFieldWordVo fieldVo);

    void insertFieldOffset(FullTextIndexOffsetVo offsetVo);

    void insertContent(FullTextIndexContentVo contentVo);

    void deleteFullTextIndexByTargetIdAndType(FullTextIndexVo fullTextIndexVo);
}
