/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the Sustainable Use License (SUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.store.qdrant;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface IQdrantCollection<T> {

    /*集合名称*/
    String getName();

    String getLabel();

    /*
        重建向量库
         */
    void rebuildPoint(boolean isAll);

    /**
     * 写入数据
     *
     */
    //void upsertVectors(Long targetId, List<Points.PointStruct> points) throws ExecutionException, InterruptedException, TimeoutException;

    /**
     * 写入单个业务对象对应的向量
     */
    void upsertPoint(Long targetId, T target) throws ExecutionException, InterruptedException, TimeoutException;

    /*
    初始化集合
     */
    void initCollection();

    /*
    删除集合
     */
    void deleteCollection();

    /**
     * 搜索相似向量
     */
    List<T> searchSimilarTarget(T target, float score, int limit, long minuteBefore);


    int searchSimilarTargetCount(T target, float score, int limit, long minuteBefore);

    /*
    获取 point 数量
     */
    int getPointCount();

    /**
     * 返回向量维度
     */
    int getVectorSize();

    /*
    删除向量
     */
    void deletePoint(Long id);
}
