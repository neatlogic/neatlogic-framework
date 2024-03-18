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
