/*
 * Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
