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

package neatlogic.framework.fulltextindex.core;

import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.asynchronization.threadpool.CachedThreadPool;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.fulltextindex.dao.mapper.FullTextIndexMapper;
import neatlogic.framework.fulltextindex.dao.mapper.FullTextIndexRebuildAuditMapper;
import neatlogic.framework.fulltextindex.dao.mapper.FullTextIndexWordMapper;
import neatlogic.framework.fulltextindex.dto.fulltextindex.*;
import neatlogic.framework.fulltextindex.dto.globalsearch.DocumentVo;
import neatlogic.framework.fulltextindex.enums.Status;
import neatlogic.framework.healthcheck.dao.mapper.DatabaseFragmentMapper;
import neatlogic.framework.transaction.core.AfterTransactionJob;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.dom4j.*;
import org.dom4j.tree.DefaultText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.tidy.Tidy;

import javax.annotation.Resource;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public abstract class FullTextIndexHandlerBase implements IFullTextIndexHandler {
    private static final Logger logger = LoggerFactory.getLogger(FullTextIndexHandlerBase.class);
    static Tidy tidy = new Tidy();

    static {
        tidy.setPrintBodyOnly(true);
        tidy.setXmlOut(true);
        tidy.setFixBackslash(true);
        tidy.setQuoteNbsp(true);
        tidy.setShowWarnings(false);
        // tidy.setUpperCaseAttrs(true);
        // tidy.setUpperCaseTags(true);
        tidy.setRawOut(true);
    }

    @Resource
    private FullTextIndexWordMapper fullTextIndexWordMapper;

    @Resource
    private FullTextIndexMapper fullTextIndexMapper;

    @Resource
    private FullTextIndexRebuildAuditMapper fullTextIndexRebuildAuditMapper;

    @Resource
    private DatabaseFragmentMapper databaseFragmentMapper;

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
        AfterTransactionJob<FullTextIndexVo> job = new AfterTransactionJob<>("FULLTEXTINDEX-DELETE-" + this.getType().getType().toUpperCase(Locale.ROOT) + "-" + targetId);
        String moduleId = this.getModuleId();
        job.execute(new FullTextIndexVo(targetId, this.getType().getType()), fullTextIndexVo -> fullTextIndexMapper.deleteFullTextIndexByTargetIdAndType(new FullTextIndexVo(targetId, this.getType().getType()), moduleId));
    }


    /**
     * 给重建索引使用的方法，以同步方式执行索引创建
     *
     * @param targetId 目标id
     * @param isSync   是否同步
     */
    protected final void createIndex(Long targetId, boolean isSync) {
        AfterTransactionJob<FullTextIndexVo> job = new AfterTransactionJob<>("FULLTEXTINDEX-CREATE-" + this.getType().getType().toUpperCase(Locale.ROOT) + "-" + targetId);
        String moduleId = this.getModuleId();
        job.execute(new FullTextIndexVo(targetId, this.getType().getType()), fullTextIndexVo -> {
            //删除索引
            fullTextIndexMapper.deleteFullTextIndexByTargetIdAndType(fullTextIndexVo, moduleId);

            //写入目标表
            FullTextIndexTargetVo fullTextIndexTargetVo = new FullTextIndexTargetVo();
            fullTextIndexTargetVo.setModuleId(this.getModuleId());
            fullTextIndexTargetVo.setTargetId(targetId);
            fullTextIndexTargetVo.setTargetType(this.getType().getType());
            fullTextIndexMapper.insertTarget(fullTextIndexTargetVo);
            try {
                myCreateIndex(fullTextIndexVo);

                // 一定要在这里设置，不然后面都取不到targetType
                fullTextIndexVo.setTargetType(fullTextIndexVo.getTargetType());

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
                                fullTextIndexMapper.replaceIntoField(fieldVo, moduleId);
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
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                if (ex instanceof ApiRuntimeException) {
                    fullTextIndexTargetVo.setError(((ApiRuntimeException) ex).getMessage());
                } else {
                    fullTextIndexTargetVo.setError(ex.getMessage());
                }
                fullTextIndexMapper.updateTargetError(fullTextIndexTargetVo);
            }
        }, isSync);
    }

    @Override
    public final void createIndex(Long targetId) {
        createIndex(targetId, false);
    }

    protected abstract void myCreateIndex(FullTextIndexVo fullTextIndexVo);

    private static void replaceKeyword(Element fatherElement, List<FullTextIndexWordVo> wordList) {
        List<Node> contentList = fatherElement.content();
        String mergeText = "";
        List<Node> newContentList = new ArrayList<>();
        for (Node node : contentList) {
            if (node instanceof Text) {
                Text text = (Text) node;
                mergeText += text.getText();
            } else if (node instanceof Element) {
                if (StringUtils.isNotEmpty(mergeText)) {
                    Text t = new DefaultText(mergeText);
                    newContentList.add(t);
                    mergeText = "";
                }
                Element element = (Element) node;
                replaceKeyword(element, wordList);
                newContentList.add(element);
            }
        }
        if (StringUtils.isNotEmpty(mergeText)) {
            Text t = new DefaultText(mergeText);
            newContentList.add(t);
            mergeText = "";
        }

        for (FullTextIndexWordVo w : wordList) {
            List<Node> finalContentList = new ArrayList<>();
            for (Node content : newContentList) {
                if (content instanceof Text) {
                    Text text = (Text) content;
                    String c = text.getText();

                    while (c.toLowerCase().contains(w.getWord().toLowerCase())) {
                        // 关键字在开头
                        if (c.toLowerCase().indexOf(w.getWord().toLowerCase()) == 0) {
                            Element hlElement = DocumentHelper.createElement("span");
                            hlElement.addAttribute("class", "highlight");
                            hlElement.addText(c.substring(0, w.getWord().length()));
                            finalContentList.add(hlElement);
                            c = c.substring(w.getWord().length());
                        } else {// 关键字在中间
                            finalContentList.add(new DefaultText(c.substring(0, c.toLowerCase().indexOf(w.getWord().toLowerCase()))));
                            c = c.substring(c.toLowerCase().indexOf(w.getWord().toLowerCase()));
                        }
                    }
                    // 处理剩下的文本
                    if (StringUtils.isNotEmpty(c)) {
                        finalContentList.add(new DefaultText(c));
                    }

                } else {
                    finalContentList.add(content);
                }
            }
            // 利用新的contentList重新计算关键字位置
            newContentList = finalContentList;
        }
        fatherElement.setContent(newContentList);
    }


    @Override
    public final void makeupDocument(DocumentVo documentVo) {
        myMakeupDocument(documentVo);
        if (StringUtils.isNotBlank(documentVo.getContent())) {
            StringWriter sw = new StringWriter();
            tidy.parseDOM(new StringReader(documentVo.getContent()), sw);
            String content = sw.toString();
            content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<content>\n" + content + "\n</content>";
            Document document;
            try {
                document = DocumentHelper.parseText(content);
                Element rootElement = document.getRootElement();
                documentVo.getFullTextIndexWordList().sort((o1, o2) -> {
                    /* 关键字从长到短排序 **/
                    return o2.getWord().length() - o1.getWord().length();
                });
                replaceKeyword(rootElement, documentVo.getFullTextIndexWordList());
                content = rootElement.asXML();
                content = content.replace("\n", "").replace("<content>", "").replace("</content>", "");
                documentVo.setHighlightContent(content);
            } catch (DocumentException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    protected abstract void myMakeupDocument(DocumentVo documentVo);

    public final void rebuildIndex(String type, Boolean isRebuildAll) {
        FullTextIndexTypeVo fullTextIndexTypeVo = FullTextIndexHandlerFactory.getTypeByName(type);
        FullTextIndexRebuildAuditVo auditVo = new FullTextIndexRebuildAuditVo();
        auditVo.setType(type);
        auditVo.setEditor(UserContext.get().getUserUuid(true));
        auditVo.setStatus(Status.DOING.getValue());
        fullTextIndexRebuildAuditMapper.insertFullTextIndexRebuildAudit(auditVo);

        if (isRebuildAll) {
            //删除所有已经创建的索引
            //fullTextIndexMapper.deleteFullTextIndexByType(fullTextIndexTypeVo);
            //!!!!!注意：以下直接使用truncate语句
            databaseFragmentMapper.truncateTable(TenantContext.get().getDbName(), "fulltextindex_target_" + fullTextIndexTypeVo.getModuleId());
            databaseFragmentMapper.truncateTable(TenantContext.get().getDbName(), "fulltextindex_field_" + fullTextIndexTypeVo.getModuleId());
            databaseFragmentMapper.truncateTable(TenantContext.get().getDbName(), "fulltextindex_offset_" + fullTextIndexTypeVo.getModuleId());
            databaseFragmentMapper.truncateTable(TenantContext.get().getDbName(), "fulltextindex_content_" + fullTextIndexTypeVo.getModuleId());

        }
        CachedThreadPool.execute(new RebuildRunner(fullTextIndexTypeVo, auditVo));
    }

    class RebuildRunner extends NeatLogicThread {
        private final FullTextIndexRebuildAuditVo auditVo;
        private final FullTextIndexTypeVo fullTextIndexTypeVo;

        public RebuildRunner(FullTextIndexTypeVo _fullTextIndexTypeVo, FullTextIndexRebuildAuditVo _auditVo) {
            super("FULLTEXTINDEX-REBUILDER");
            auditVo = _auditVo;
            fullTextIndexTypeVo = _fullTextIndexTypeVo;
        }

        @Override
        protected void execute() {
            try {
                myRebuildIndex(fullTextIndexTypeVo);
            } catch (Exception ex) {
                if (ex instanceof ApiRuntimeException) {
                    auditVo.setError(((ApiRuntimeException) ex).getMessage());
                } else {
                    auditVo.setError(ExceptionUtils.getStackTrace(ex));
                }
            }
            auditVo.setStatus(Status.DONE.getValue());
            fullTextIndexRebuildAuditMapper.updateFullTextIndexRebuildAuditStatus(auditVo);
        }
    }

    protected abstract void myRebuildIndex(FullTextIndexTypeVo fullTextIndexTypeVo);
}
