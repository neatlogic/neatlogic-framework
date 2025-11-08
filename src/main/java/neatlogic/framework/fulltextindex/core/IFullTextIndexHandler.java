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

package neatlogic.framework.fulltextindex.core;


import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.fulltextindex.dto.globalsearch.DocumentVo;

import java.util.concurrent.Semaphore;

public interface IFullTextIndexHandler {
    //是否需要在全文检索中保存内容
    default boolean needSaveContent() {
        return false;
    }

    IFullTextIndexType getType();

    /**
     * 初始化专有名词入字典
     */
    <T> void initialTerms(T param);


    /**
     * 添加专有名词入字典
     */
    default void addTerms(String... term) {
    }

    /*
     * @Description: 创建索引
     * @Author: chenqiwei
     * @Date: 2021/2/25 4:56 下午
     * @Params: [target, isAsync]
     * @Returns: void
     **/
    void createIndex(Long targetId);

    void createIndex(Long targetId, JSONObject dataObj);

    void createIndex(Long targetId, Semaphore lock);

    /*
     * @Description: 删除索引
     * @Author: chenqiwei
     * @Date: 2021/2/25 5:03 下午
     * @Params: [targetId]
     * @Returns: void
     **/
    void deleteIndex(Long targetId);

    /*
     * @Description: 重建索引
     * @Author: chenqiwei
     * @Date: 2021/2/25 5:03 下午
     * @Params: [isRebuildAll]
     * @Returns: void
     **/
    void rebuildIndex(String type, Boolean isRebuildAll);

    /**
     * 格式化搜索中心搜索结果
     *
     * @param documentVo 原始搜索结果
     */
    void makeupDocument(DocumentVo documentVo);
}
