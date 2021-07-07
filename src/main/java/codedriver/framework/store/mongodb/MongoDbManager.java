/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.store.mongodb;

import codedriver.framework.common.RootComponent;
import codedriver.framework.dao.mapper.MongoDbMapper;
import codedriver.framework.dto.MongoDbVo;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

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

    @PostConstruct
    public void init() {
        List<MongoDbVo> mongoDbList = mongoDbMapper.getAllActiveTenantMongoDb();
        for (MongoDbVo mongoDbVo : mongoDbList) {
            if (!mongoDbMap.containsKey(mongoDbVo.getTenantUuid())) {
                MongoClient client = MongoClients.create("mongodb://" + mongoDbVo.getUsername() + ":" + mongoDbVo.getPasswordPlain() + "@" + mongoDbVo.getHost() + ":" + mongoDbVo.getPort() + "/" + mongoDbVo.getDatabase() + "?authSource=admin");
                mongoDbMap.put(mongoDbVo.getTenantUuid(), client);
            }
        }
    }

    public static MongoClient getMongoClient(String tenantUuid) {
        return mongoDbMap.get(tenantUuid);
    }
}
