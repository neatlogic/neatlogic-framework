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

package neatlogic.framework.util.mongodb;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MongoService {

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 创建集合并建立唯一索引
     */
    public void createCollectionAndUniqueIndex(String collectionName, String indexKey, String indexName) {

        // 检查集合是否已存在
        if (!mongoTemplate.collectionExists(collectionName)) {
            // 创建集合
            mongoTemplate.createCollection(collectionName);
            //System.out.println("集合创建成功: " + collectionName);
        } else {
            //System.out.println("集合已存在: " + collectionName);
        }

        // 在集合上创建唯一索引
        Index index = new Index()
                .named(indexName)
                .on(indexKey, Sort.Direction.ASC) // 索引字段替换为实际字段名称
                .unique(); // 设置唯一约束

        mongoTemplate.indexOps(collectionName).ensureIndex(index);
        //System.out.println("唯一索引创建成功: id");


        Index uniqueIndex = new Index()
                .named("unique_ip_port")
                .on("ip", Sort.Direction.ASC)
                .on("port", Sort.Direction.ASC)
                .unique();

        mongoTemplate.indexOps(collectionName).ensureIndex(uniqueIndex);
        //System.out.println("唯一索引 unique_ip_port 创建成功");
    }
}

