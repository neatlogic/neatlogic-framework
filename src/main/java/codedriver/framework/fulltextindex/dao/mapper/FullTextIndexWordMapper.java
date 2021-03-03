package codedriver.framework.fulltextindex.dao.mapper;

import codedriver.framework.fulltextindex.dto.FullTextIndexWordVo;

/**
 * @Title: FullTextIndexWordMapper
 * @Package: codedriver.framework.fulltextindex.dao.mapper
 * @Description: TODO
 * @author: chenqiwei
 * @date: 2021/2/256:16 下午
 * Copyright(c) 2021 TechSure Co.,Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public interface FullTextIndexWordMapper {
    FullTextIndexWordVo getWordByWord(String word);

    int insertWord(FullTextIndexWordVo fullTextIndexWordVo);
}
