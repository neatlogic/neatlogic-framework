/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.store.mongodb;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import com.mongodb.client.MongoDatabase;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

public class CodedriverMongoDbFactory extends SimpleMongoClientDatabaseFactory {

    public CodedriverMongoDbFactory(String connectionString) {
        super(connectionString);
    }


    @Override
    protected MongoDatabase doGetMongoDatabase(String dbName) {
        return MongoDbManager.getMongoClient(TenantContext.get().getTenantUuid()).getDatabase(MongoDbManager.getDatabase(TenantContext.get().getTenantUuid()));
    }


}
