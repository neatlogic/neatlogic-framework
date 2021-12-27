/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.globalsearch.core;

import codedriver.framework.batch.BatchRunner;
import codedriver.framework.common.util.PageUtil;
import codedriver.framework.fulltextindex.core.FullTextIndexHandlerFactory;
import codedriver.framework.fulltextindex.core.IFullTextIndexHandler;
import codedriver.framework.fulltextindex.dto.fulltextindex.FullTextIndexTypeVo;
import codedriver.framework.fulltextindex.dto.fulltextindex.FullTextIndexWordVo;
import codedriver.framework.fulltextindex.dto.globalsearch.DocumentTypeVo;
import codedriver.framework.fulltextindex.dto.globalsearch.DocumentVo;
import codedriver.framework.globalsearch.dao.mapper.DocumentMapper;
import codedriver.framework.globalsearch.dao.mapper.WordMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
        List<DocumentTypeVo> documentTypeList = new ArrayList<>();
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
                    runner.execute(pDocumentList, pDocumentList.size(), item -> {
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
