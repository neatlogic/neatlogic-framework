/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the Sustainable Use License (SUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.store.qdrant;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.dao.mapper.QdrantMapper;
import neatlogic.framework.dto.QdrantVo;
import neatlogic.framework.exception.qdrant.QdrantClientInitFailedException;
import neatlogic.framework.util.SpringContextUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RootComponent
public class QdrantClientFactory {
    private static final Map<String, QdrantClient> qdrantClientMap = new HashMap<>();


    public static QdrantClient getClient() {
        QdrantClient client;
        synchronized (QdrantClientFactory.class) {
            if (!qdrantClientMap.containsKey(TenantContext.get().getTenantUuid())) {
                QdrantVo qdrantVo = SpringContextUtil.getBean(QdrantMapper.class).getQdrantByTenant(TenantContext.get().getTenantUuid());
                if (qdrantVo != null && StringUtils.isNotBlank(qdrantVo.getUrl())) {
                    ManagedChannel channel = Grpc.newChannelBuilder(
                            qdrantVo.getUrl(), InsecureChannelCredentials.create()
                    ).build();

                    if (StringUtils.isNotBlank(qdrantVo.getApiKey())) {
                        client = new QdrantClient((QdrantGrpcClient.newBuilder(channel)).withApiKey(qdrantVo.getApiKey()).build());
                        qdrantClientMap.put(TenantContext.get().getTenantUuid(), client);
                    } else {
                        client = new QdrantClient((QdrantGrpcClient.newBuilder(channel)).build());
                        qdrantClientMap.put(TenantContext.get().getTenantUuid(), client);
                    }

                    List<IQdrantCollection> collectionList = QdrantCollectionFactory.getAllCollection();
                    for (IQdrantCollection collection : collectionList) {
                        collection.initCollection();
                    }
                }
            }
        }
        client = qdrantClientMap.get(TenantContext.get().getTenantUuid());
        if (client == null) {
            throw new QdrantClientInitFailedException();
        }
        return client;
    }
}
