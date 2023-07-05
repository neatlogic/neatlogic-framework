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

package neatlogic.framework.store.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.dao.mapper.MongoDbMapper;
import neatlogic.framework.dto.MongoDbVo;
import org.apache.commons.lang3.StringUtils;

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
                MongoClient client = MongoClients.create("mongodb://" + mongoDbVo.getUsername() + ":" + mongoDbVo.getPasswordPlain() + "@" + mongoDbVo.getHost() + "/" + mongoDbVo.getDatabase() + (StringUtils.isNotBlank(mongoDbVo.getOption()) ? "?" + mongoDbVo.getOption() : ""));
                mongoDbMap.put(mongoDbVo.getTenantUuid(), client);
                mongoDatabaseMap.put(mongoDbVo.getTenantUuid(), mongoDbVo.getDatabase());
            }
        }
    }

    public static void addDynamicDataSource(MongoDbVo mongoDbVo) {
        MongoClient client = MongoClients.create("mongodb://" + mongoDbVo.getUsername() + ":" + mongoDbVo.getPasswordPlain() + "@" + mongoDbVo.getHost() + "/" + mongoDbVo.getDatabase() + (StringUtils.isNotBlank(mongoDbVo.getOption()) ? "?" + mongoDbVo.getOption() : ""));
        mongoDbMap.put(mongoDbVo.getTenantUuid(), client);
        mongoDatabaseMap.put(mongoDbVo.getTenantUuid(), mongoDbVo.getDatabase());
    }

    public static String getDatabase(String tenantUuid) {
        return mongoDatabaseMap.get(tenantUuid);
    }

    public static MongoClient getMongoClient(String tenantUuid) {
        return mongoDbMap.get(tenantUuid);
    }
}
