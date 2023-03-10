/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.fulltextindex.core;

import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.asynchronization.threadpool.CachedThreadPool;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.fulltextindex.dao.mapper.FullTextIndexMapper;
import neatlogic.framework.fulltextindex.dao.mapper.FullTextIndexRebuildAuditMapper;
import neatlogic.framework.fulltextindex.dao.mapper.FullTextIndexWordMapper;
import neatlogic.framework.fulltextindex.dto.fulltextindex.*;
import neatlogic.framework.fulltextindex.dto.globalsearch.DocumentVo;
import neatlogic.framework.fulltextindex.enums.Status;
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
import java.util.*;

public abstract class FullTextIndexHandlerBase implements IFullTextIndexHandler {
    private final static Logger logger = LoggerFactory.getLogger(FullTextIndexHandlerBase.class);
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

    /*
     * @Description: ??????????????????mybatis?????????????????????????????????????????????????????????????????????????????????
     * @Author: chenqiwei
     * @Date: 2021/3/1 5:09 ??????
     * @Params: []
     * @Returns: java.lang.String
     **/
    protected abstract String getModuleId();

    @Override
    public void deleteIndex(Long targetId) {
       /* //??????threadlocal?????????????????????????????????
        FullTextIndexModuleContainer.set(this.getModuleId());*/
        fullTextIndexMapper.deleteFullTextIndexByTargetIdAndType(new FullTextIndexVo(targetId, this.getType().getType()), this.getModuleId());
    }

    /**
     * ??????????????????????????????????????????????????????????????????
     *
     * @param targetId ??????id
     * @param isSync   ????????????
     */
    protected final void createIndex(Long targetId, boolean isSync) {
        AfterTransactionJob<FullTextIndexVo> job = new AfterTransactionJob<>("FULLTEXTINDEX-CREATE-" + this.getType().getType().toUpperCase(Locale.ROOT) + "-" + targetId);
        String moduleId = this.getModuleId();
        job.execute(new FullTextIndexVo(targetId, this.getType().getType()), fullTextIndexVo -> {
            //????????????
            fullTextIndexMapper.deleteFullTextIndexByTargetIdAndType(fullTextIndexVo, moduleId);

            //???????????????
            FullTextIndexTargetVo fullTextIndexTargetVo = new FullTextIndexTargetVo();
            fullTextIndexTargetVo.setModuleId(this.getModuleId());
            fullTextIndexTargetVo.setTargetId(targetId);
            fullTextIndexTargetVo.setTargetType(this.getType().getType());
            fullTextIndexMapper.insertTarget(fullTextIndexTargetVo);
            try {
                myCreateIndex(fullTextIndexVo);

                // ???????????????????????????????????????????????????targetType
                fullTextIndexVo.setTargetType(fullTextIndexVo.getTargetType());

                //???????????????
                List<FullTextIndexWordVo> wordList = fullTextIndexVo.getWordList();
                if (CollectionUtils.isNotEmpty(wordList)) {
                    for (FullTextIndexWordVo wordVo : wordList) {
                        fullTextIndexWordMapper.insertWord(wordVo);
                    }
                }

                //???????????????
                List<FullTextIndexContentVo> contentList = fullTextIndexVo.getContentList();
                if (CollectionUtils.isNotEmpty(contentList)) {
                    for (FullTextIndexContentVo contentVo : contentList) {
                        fullTextIndexMapper.insertContent(contentVo, moduleId);
                    }
                }

                //????????????????????????????????????
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
                        // ??????????????????
                        if (c.toLowerCase().indexOf(w.getWord().toLowerCase()) == 0) {
                            Element hlElement = DocumentHelper.createElement("span");
                            hlElement.addAttribute("class", "highlight");
                            hlElement.addText(c.substring(0, w.getWord().length()));
                            finalContentList.add(hlElement);
                            c = c.substring(w.getWord().length());
                        } else {// ??????????????????
                            finalContentList.add(new DefaultText(c.substring(0, c.toLowerCase().indexOf(w.getWord().toLowerCase()))));
                            c = c.substring(c.toLowerCase().indexOf(w.getWord().toLowerCase()));
                        }
                    }
                    // ?????????????????????
                    if (StringUtils.isNotEmpty(c)) {
                        finalContentList.add(new DefaultText(c));
                    }

                } else {
                    finalContentList.add(content);
                }
            }
            // ????????????contentList???????????????????????????
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
                    /* ??????????????????????????? **/
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
            //?????????????????????????????????
            fullTextIndexMapper.deleteFullTextIndexByType(fullTextIndexTypeVo);
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
