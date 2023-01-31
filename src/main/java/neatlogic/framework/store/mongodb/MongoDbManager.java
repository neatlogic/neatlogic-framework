/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.store.mongodb;

import neatlogic.framework.common.RootComponent;
import neatlogic.framework.dao.mapper.MongoDbMapper;
import neatlogic.framework.dto.MongoDbVo;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
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

    public static String getDatabase(String tenantUuid) {
        return mongoDatabaseMap.get(tenantUuid);
    }

    public static MongoClient getMongoClient(String tenantUuid) {
        return mongoDbMap.get(tenantUuid);
    }
}
