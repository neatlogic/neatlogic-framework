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

import com.mongodb.ClientSessionOptions;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.exception.database.TenantMongoClientNotFoundException;
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
        MongoClient mongoClient = MongoDbManager.getMongoClient(TenantContext.get().getTenantUuid());
        if(mongoClient == null){
            throw new TenantMongoClientNotFoundException();
        }
        return mongoClient.getDatabase(dbName);
    }

    @Override
    public @NonNull ClientSession getSession(@NonNull ClientSessionOptions options) {
        // 确保 MongoClient 是从租户管理中获取的
        MongoClient mongoClient = MongoDbManager.getMongoClient(TenantContext.get().getTenantUuid());
        if(mongoClient == null){
            throw new TenantMongoClientNotFoundException();
        }
        return mongoClient.startSession(options);
    }

    @Override
    public @NonNull MongoExceptionTranslator getExceptionTranslator() {
        return new MongoExceptionTranslator();
    }

    @Override
    public @NonNull MongoDatabaseFactory withSession(@NonNull ClientSession session) {
        MongoClient mongoClient = MongoDbManager.getMongoClient(TenantContext.get().getTenantUuid());
        if(mongoClient == null){
            throw new TenantMongoClientNotFoundException();
        }
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

