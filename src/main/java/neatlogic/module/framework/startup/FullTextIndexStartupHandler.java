/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.module.framework.startup;

import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.fulltextindex.core.FullTextIndexHandlerFactory;
import neatlogic.framework.fulltextindex.core.IFullTextIndexHandler;
import neatlogic.framework.fulltextindex.dao.mapper.FullTextIndexDictMapper;
import neatlogic.framework.fulltextindex.dto.fulltextindex.FullTextIndexTypeVo;
import neatlogic.framework.fulltextindex.dto.fulltextindex.FullTextIndexWordVo;
import neatlogic.framework.fulltextindex.utils.FullTextIndexUtil;
import neatlogic.framework.startup.StartupBase;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class FullTextIndexStartupHandler extends StartupBase {

    @Resource
    private FullTextIndexDictMapper fullTextIndexDictMapper;

    /**
     * 作业名称
     *
     * @return 字符串
     */
    @Override
    public String getName() {
        return "添加专有名词进全文检索字典";
    }

    /**
     * 每个租户分别执行
     */
    @Override
    public int executeForCurrentTenant() {
        List<FullTextIndexTypeVo> typeList = FullTextIndexHandlerFactory.getAllTypeList();
        if (CollectionUtils.isNotEmpty(typeList)) {
            for (FullTextIndexTypeVo typeVo : typeList) {
                IFullTextIndexHandler handler = FullTextIndexHandlerFactory.getHandler(typeVo.getType());
                if (handler != null) {
                    handler.initialTerms(null);
                }
            }
        }
        //补充自定义字典
        BasePageVo pageVo = new BasePageVo();
        pageVo.setCurrentPage(1);
        pageVo.setPageSize(100);
        List<FullTextIndexWordVo> wordList = fullTextIndexDictMapper.searchDictionary(pageVo);
        while (CollectionUtils.isNotEmpty(wordList)) {
            for (FullTextIndexWordVo wordVo : wordList) {
                FullTextIndexUtil.addWord(wordVo.getWord());
            }
            pageVo.setCurrentPage(pageVo.getCurrentPage() + 1);
            wordList = fullTextIndexDictMapper.searchDictionary(pageVo);
        }
        return 0;
    }


    /**
     * 排序
     *
     * @return 顺序
     */
    @Override
    public int sort() {
        return 99;
    }
}
