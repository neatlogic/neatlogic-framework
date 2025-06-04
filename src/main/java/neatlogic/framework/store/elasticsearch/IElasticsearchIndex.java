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

package neatlogic.framework.store.elasticsearch;

import neatlogic.framework.dto.elasticsearch.IndexResultVo;

import java.util.Map;

public interface IElasticsearchIndex<T> {
    /*
    ES中的索引名称
     */
    String getIndexName();

    /*
    插件唯一标识
     */
    String getName();

    /*
    插件名称
     */
    String getLabel();

    /*
    创建索引
     */
    void createIndex();

    /*
    删除索引
     */
    void deleteIndex();

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
