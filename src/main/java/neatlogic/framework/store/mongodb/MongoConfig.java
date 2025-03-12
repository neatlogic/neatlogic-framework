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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

    @Bean
    public NeatlogicMongoDatabaseFactory neatlogicMongoDatabaseFactory() {
        return new NeatlogicMongoDatabaseFactory();
    }

    @Bean
    public MongoTemplate mongoTemplate(NeatlogicMongoDatabaseFactory factory) {
        return new MongoTemplate(factory);
    }

    @Bean
    public MongoTransactionManager transactionManagerMongoDb(NeatlogicMongoDatabaseFactory factory) {
        return new MongoTransactionManager(factory); // 使用标准事务管理器‌:ml-citation{ref="2,3" data="citationList"}
    }
}
