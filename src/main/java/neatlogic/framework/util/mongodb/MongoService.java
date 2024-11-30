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
    }
}

