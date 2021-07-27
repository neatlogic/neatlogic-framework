/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.store.mongodb;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

//@Configuration
public class CodedriverMongoDbFactory extends SimpleMongoClientDatabaseFactory {
    //@Autowired
    //MongoDataSources mongoDataSources;

    /*public CodedriverMongoDbFactory(@Qualifier("getMongoClient") MongoClient mongoClient, String databaseName) {
        super(mongoClient, databaseName);
    }*/

    public CodedriverMongoDbFactory(MongoClient mongoClient, String databaseName) {
        super(mongoClient, databaseName);
    }

    @Override
    protected MongoDatabase doGetMongoDatabase(String dbName) {
        return MongoDbManager.getMongoClient(TenantContext.get().getTenantUuid()).getDatabase(MongoDbManager.getDatabase(TenantContext.get().getTenantUuid()));
    }


}
