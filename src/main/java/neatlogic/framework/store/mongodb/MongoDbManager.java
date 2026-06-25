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

package neatlogic.framework.store.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.dao.mapper.MongoDbMapper;
import neatlogic.framework.dto.MongoDbVo;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RootComponent
public class MongoDbManager {
    @Resource
    private MongoDbMapper mongoDbMapper;

    private static final Map<String, MongoClient> mongoDbMap = new HashMap<>();
    private static final Map<String, String> mongoDatabaseMap = new HashMap<>();

    @PostConstruct
    public void init() {
        List<MongoDbVo> mongoDbList = mongoDbMapper.getAllActiveTenantMongoDb();
        for (MongoDbVo mongoDbVo : mongoDbList) {
            if (!mongoDbMap.containsKey(mongoDbVo.getTenantUuid())) {
                addDynamicDataSource(mongoDbVo);
            }
        }
    }

    public static void addDynamicDataSource(MongoDbVo mongoDbVo) {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString("mongodb://"
                        + mongoDbVo.getUsername() + ":"
                        + mongoDbVo.getPasswordPlain() + "@"
                        + mongoDbVo.getHost() + "/"
                        + mongoDbVo.getDatabase() + "?"
                        + mongoDbVo.getOption()))
                .writeConcern(WriteConcern.MAJORITY)  // 添加这一行
                .build();
        MongoClient client = MongoClients.create(settings);

        mongoDbMap.put(mongoDbVo.getTenantUuid(), client);
        mongoDatabaseMap.put(mongoDbVo.getTenantUuid(), mongoDbVo.getDatabase());
    }

    /**
     * 关闭并移除指定租户的Mongo客户端和库名缓存。
     * 租户禁用、删除时需要释放客户端，重新启用时再按主库配置重建。
     *
     * @param tenantUuid 租户uuid
     */
    public static synchronized void removeDynamicDataSource(String tenantUuid) {
        MongoClient mongoClient = mongoDbMap.remove(tenantUuid);
        if (mongoClient != null) {
            mongoClient.close();
        }
        mongoDatabaseMap.remove(tenantUuid);
    }

    public static String getDatabase(String tenantUuid) {
        return mongoDatabaseMap.get(tenantUuid);
    }

    public static MongoClient getMongoClient(String tenantUuid) {
        return mongoDbMap.get(tenantUuid);
    }
}
