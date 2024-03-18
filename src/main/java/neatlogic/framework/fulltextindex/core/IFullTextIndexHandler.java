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


import neatlogic.framework.fulltextindex.dto.globalsearch.DocumentVo;

public interface IFullTextIndexHandler {

    IFullTextIndexType getType();


    /*
     * @Description: 创建索引
     * @Author: chenqiwei
     * @Date: 2021/2/25 4:56 下午
     * @Params: [target, isAsync]
     * @Returns: void
     **/
    void createIndex(Long targetId);

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
