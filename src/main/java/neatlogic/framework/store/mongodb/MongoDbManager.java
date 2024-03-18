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
