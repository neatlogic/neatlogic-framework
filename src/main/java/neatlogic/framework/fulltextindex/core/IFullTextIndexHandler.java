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
