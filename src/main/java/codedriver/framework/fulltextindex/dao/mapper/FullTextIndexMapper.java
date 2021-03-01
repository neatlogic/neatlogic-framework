package codedriver.framework.fulltextindex.dao.mapper;

import codedriver.framework.fulltextindex.dto.FullTextIndexContentVo;
import codedriver.framework.fulltextindex.dto.FullTextIndexFieldWordVo;
import codedriver.framework.fulltextindex.dto.FullTextIndexOffsetVo;
import codedriver.framework.fulltextindex.dto.FullTextIndexVo;

public interface FullTextIndexMapper {
    void insertField(FullTextIndexFieldWordVo fieldVo);

    void insertFieldOffset(FullTextIndexOffsetVo offsetVo);

    void insertContent(FullTextIndexContentVo contentVo);

    void deleteFullTextIndexByTargetIdAndType(FullTextIndexVo fullTextIndexVo);
}
