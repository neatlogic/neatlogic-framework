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

package neatlogic.framework.globalsearch.core;

import neatlogic.framework.batch.BatchRunner;
import neatlogic.framework.common.util.PageUtil;
import neatlogic.framework.fulltextindex.core.FullTextIndexHandlerFactory;
import neatlogic.framework.fulltextindex.core.IFullTextIndexHandler;
import neatlogic.framework.fulltextindex.dto.fulltextindex.FullTextIndexTypeVo;
import neatlogic.framework.fulltextindex.dto.fulltextindex.FullTextIndexWordVo;
import neatlogic.framework.fulltextindex.dto.globalsearch.DocumentTypeVo;
import neatlogic.framework.fulltextindex.dto.globalsearch.DocumentVo;
import neatlogic.framework.globalsearch.dao.mapper.DocumentMapper;
import neatlogic.framework.globalsearch.dao.mapper.WordMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class GlobalSearchManager {
    Logger logger = LoggerFactory.getLogger(GlobalSearchManager.class);

    private static WordMapper wordMapper;

    private static DocumentMapper documentMapper;

    @Autowired
    public GlobalSearchManager(WordMapper _wordMapper, DocumentMapper _documentMapper) {
        wordMapper = _wordMapper;
        documentMapper = _documentMapper;
    }


    public static List<DocumentTypeVo> searchDocument(DocumentVo documentVo) {
        //获取当前租户拥有索引类型的模块列表
        List<String> moduleIdList = FullTextIndexHandlerFactory.getModuleIdList();
        List<DocumentTypeVo> documentTypeList = Collections.synchronizedList(new ArrayList<>());
        if (CollectionUtils.isNotEmpty(moduleIdList) && CollectionUtils.isNotEmpty(documentVo.getTypeList()) && StringUtils.isNotBlank(documentVo.getKeyword()) && CollectionUtils.isNotEmpty(documentVo.getWordList())) {
            //根据分词加过查询是否有命中分词
            List<FullTextIndexWordVo> wordList = wordMapper.searchWord(new ArrayList<>(documentVo.getWordList()));
            if (CollectionUtils.isNotEmpty(wordList)) {
                List<DocumentVo> pDocumentList = new ArrayList<>();
                List<FullTextIndexTypeVo> typeList = FullTextIndexHandlerFactory.getAllTypeList();
                for (String type : documentVo.getTypeList()) {
                    Optional<FullTextIndexTypeVo> op = typeList.stream().filter(fullTextIndexType -> fullTextIndexType.getType().equals(type)).findFirst();
                    if (op.isPresent()) {
                        FullTextIndexTypeVo fullTextIndexTypeVo = op.get();
                        DocumentVo pDocumentVo = new DocumentVo();
                        pDocumentVo.setFullTextIndexWordList(wordList);
                        pDocumentVo.setModuleId(fullTextIndexTypeVo.getModuleId());
                        pDocumentVo.setType(fullTextIndexTypeVo.getType());
                        pDocumentVo.setCurrentPage(documentVo.getCurrentPage());
                        pDocumentVo.setPageSize(documentVo.getPageSize());
                        pDocumentList.add(pDocumentVo);
                    }
                }
                if (CollectionUtils.isNotEmpty(pDocumentList)) {
                    BatchRunner<DocumentVo> runner = new BatchRunner<>();
                    runner.execute(pDocumentList, 3, item -> {
                        List<DocumentVo> documentList = documentMapper.searchDocument(item);
                        if (CollectionUtils.isNotEmpty(documentList)) {
                            IFullTextIndexHandler handler = FullTextIndexHandlerFactory.getHandler(item.getType());
                            for (DocumentVo doc : documentList) {
                                doc.setFullTextIndexWordList(item.getFullTextIndexWordList());
                                handler.makeupDocument(doc);
                            }
                            DocumentTypeVo documentTypeVo = new DocumentTypeVo();
                            documentTypeVo.setDocumentList(documentList);
                            documentTypeVo.setType(item.getType());
                            int rowNum = documentMapper.searchDocumentCount(item);
                            documentTypeVo.setRowNum(rowNum);
                            documentTypeVo.setPageCount(PageUtil.getPageCount(rowNum, item.getPageSize()));
                            documentTypeList.add(documentTypeVo);
                        }
                    }, "GLOBALSEARCH-DOCUMENT-SEARCHER");
                }
            }
        }
        return documentTypeList;
    }

}
