/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.fulltextindex.core;

import codedriver.framework.fulltextindex.dao.mapper.FullTextIndexMapper;
import codedriver.framework.fulltextindex.dao.mapper.FullTextIndexWordMapper;
import codedriver.framework.fulltextindex.dto.*;
import codedriver.framework.transaction.core.AfterTransactionJob;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public abstract class FullTextIndexHandlerBase implements IFullTextIndexHandler {
    private final static Logger logger = LoggerFactory.getLogger(FullTextIndexHandlerBase.class);


    @Resource
    private FullTextIndexWordMapper fullTextIndexWordMapper;

    @Resource
    private FullTextIndexMapper fullTextIndexMapper;

    /*
     * @Description: 返回模块名，mybatis拦截器需要根据模块名自动选择合适表，要注意编写的正确性
     * @Author: chenqiwei
     * @Date: 2021/3/1 5:09 下午
     * @Params: []
     * @Returns: java.lang.String
     **/
    protected abstract String getModuleId();

    @Override
    public void deleteIndex(Long targetId) {
       /* //设置threadlocal告诉拦截器分配到哪个表
        FullTextIndexModuleContainer.set(this.getModuleId());*/
        fullTextIndexMapper.deleteFullTextIndexByTargetIdAndType(new FullTextIndexVo(targetId, this.getType().getType()),this.getModuleId());
    }


    @Override
    public final void createIndex(Long targetId) {
        AfterTransactionJob<FullTextIndexVo> job = new AfterTransactionJob<>();
        String moduleId = this.getModuleId();
        job.execute(new FullTextIndexVo(targetId, this.getType().getType()), fullTextIndexVo -> {
            /*
            //设置threadlocal告诉拦截器分配到哪个表
            FullTextIndexModuleContainer.set(moduleId);*/
            String oldName = Thread.currentThread().getName();
            Thread.currentThread().setName("FULLTEXTINDEX-CREATE-" + fullTextIndexVo.getTargetType().toUpperCase(Locale.ROOT) + "-" + fullTextIndexVo.getTargetId());
            try {
                myCreateIndex(fullTextIndexVo);
                // 一定要在这里设置，不然后面都取不到targetType
                fullTextIndexVo.setTargetType(fullTextIndexVo.getTargetType());
                //删除索引
                fullTextIndexMapper.deleteFullTextIndexByTargetIdAndType(fullTextIndexVo, moduleId);

                //写入分词表
                List<FullTextIndexWordVo> wordList = fullTextIndexVo.getWordList();
                if (CollectionUtils.isNotEmpty(wordList)) {
                    for (FullTextIndexWordVo wordVo : wordList) {
                        fullTextIndexWordMapper.insertWord(wordVo);
                    }
                }

                //写入内容表
                List<FullTextIndexContentVo> contentList = fullTextIndexVo.getContentList();
                if (CollectionUtils.isNotEmpty(contentList)) {
                    for (FullTextIndexContentVo contentVo : contentList) {
                        fullTextIndexMapper.insertContent(contentVo, moduleId);
                    }
                }

                //写入目标字段和分词引用表
                List<FullTextIndexFieldWordVo> fieldList = fullTextIndexVo.getFieldList();
                if (CollectionUtils.isNotEmpty(fieldList)) {
                    for (FullTextIndexFieldWordVo fieldVo : fieldList) {
                        if (StringUtils.isNotBlank(fieldVo.getWord())) {
                            FullTextIndexWordVo wordVo = fullTextIndexWordMapper.getWordByWord(fieldVo.getWord());
                            if (wordVo != null) {
                                fieldVo.setWordId(wordVo.getId());
                                fullTextIndexMapper.insertField(fieldVo, moduleId);
                                Set<FullTextIndexOffsetVo> offsetList = fieldVo.getOffsetList();
                                if (CollectionUtils.isNotEmpty(offsetList)) {
                                    for (FullTextIndexOffsetVo offsetVo : offsetList) {
                                        fullTextIndexMapper.insertFieldOffset(offsetVo, moduleId);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Throwable ex) {
                logger.error(ex.getMessage(), ex);
            } finally {
                Thread.currentThread().setName(oldName);
            }
        });
    }

    protected abstract void myCreateIndex(FullTextIndexVo fullTextIndexVo);
}
