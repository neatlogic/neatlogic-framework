/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.globalsearch.dao.mapper;

import neatlogic.framework.fulltextindex.dto.fulltextindex.FullTextIndexWordVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WordMapper {
    List<FullTextIndexWordVo> searchWord(@Param("wordList") List<String> wordList);
}
