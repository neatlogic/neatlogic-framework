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

package neatlogic.framework.store.elasticsearch;

import neatlogic.framework.dto.elasticsearch.IndexResultVo;

import java.util.Map;

public interface IElasticsearchDocument<T> extends IElasticsearchIndex {


    /*
    创建文档
     */
    void createDocument(T targetVo);


    /*
    根据目标id创建文档
     */
    void createDocument(Long targetId);

    /*
    获取文档数量
     */
    int getDocumentCount();

    /*
    删除文档
     */
    void deleteDocument(Long targetId);

    /*
    更新文档
     */
    void updateDocument(Long targetId, Map<String, Object> document, boolean isUpsert);

    /*
    手动刷新当前索引
     */
    void refresh();

    /*
    组装文档数据
     */
    Map<String, Object> makeupDocument(T targetVo);

    /*
        获取文档
         */
    T getDocument(T targetVo);


    /*
    判断是否需要分页
     */
    default Boolean needPage(T targetVo) {
        return true;
    }

    /*
    重建所有文档
     */
    void rebuildDocument(boolean isAll);

    long searchDocumentCount(T targetVo);

    /*
        搜索文档
         */
    IndexResultVo searchDocument(T targetVo, Integer currentPage, Integer pageSize);
}
