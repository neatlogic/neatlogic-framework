/*
 * Copyright (C) 2025  深圳极向量科技有限公司 All Rights Reserved.
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

package neatlogic.framework.store.mongodb;

import com.mongodb.ClientSessionOptions;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoExceptionTranslator;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class NeatlogicMongoDatabaseFactory implements MongoDatabaseFactory {

    @Override
    public @NonNull MongoDatabase getMongoDatabase() {
        return getMongoDatabase(MongoDbManager.getDatabase(TenantContext.get().getTenantUuid()));
    }

    @Override
    public @NonNull MongoDatabase getMongoDatabase(@NonNull String dbName) {
        return MongoDbManager.getMongoClient(TenantContext.get().getTenantUuid()).getDatabase(dbName);
    }

    @Override
    public @NonNull ClientSession getSession(@NonNull ClientSessionOptions options) {
        // 确保 MongoClient 是从租户管理中获取的
        return MongoDbManager.getMongoClient(TenantContext.get().getTenantUuid()).startSession(options);
    }

    @Override
    public @NonNull MongoExceptionTranslator getExceptionTranslator() {
        return new MongoExceptionTranslator();
    }

    @Override
    public @NonNull MongoDatabaseFactory withSession(@NonNull ClientSession session) {
        MongoClient mongoClient = MongoDbManager.getMongoClient(TenantContext.get().getTenantUuid());

        return new MongoDatabaseFactory() {
            @Override
            public @NonNull MongoDatabase getMongoDatabase() {
                return getMongoDatabase(MongoDbManager.getDatabase(TenantContext.get().getTenantUuid()));
            }

            @Override
            public @NonNull MongoDatabase getMongoDatabase(@NonNull String dbName) {
                return mongoClient.getDatabase(dbName);
            }

            @Override
            public @NonNull ClientSession getSession(@NonNull ClientSessionOptions options) {
                return session;
            }

            @Override
            public @NonNull MongoExceptionTranslator getExceptionTranslator() {
                return new MongoExceptionTranslator();
            }

            @Override
            public @NonNull MongoDatabaseFactory withSession(@NonNull ClientSession session) {
                return this;
            }
        };
    }
}

