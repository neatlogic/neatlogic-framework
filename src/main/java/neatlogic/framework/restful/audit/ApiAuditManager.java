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

package neatlogic.framework.restful.audit;

import neatlogic.framework.restful.dto.ApiAuditVo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
@Deprecated
//@Component
public class ApiAuditManager {
    public final static int THREAD_COUNT = 3;
    private final static int QUEUE_SIZE = 512;

    private static List<BlockingQueue<ApiAuditVo>> queueList = new ArrayList<>();

//    @PostConstruct
    public void init() {
        for (int i = 0; i < THREAD_COUNT; i++) {
            BlockingQueue<ApiAuditVo> queue = new ArrayBlockingQueue<>(QUEUE_SIZE, true);
            queueList.add(queue);
            Thread thread = new Thread(new ApiAuditThread(queue, "API-AUDIT-" + i));
            thread.setDaemon(true);
            thread.start();
        }
    }

    public static void saveAudit(ApiAuditVo apiAuditVo) {
        String tenentUuid = apiAuditVo.getTenant();
        int index = Math.abs(tenentUuid.hashCode()) % THREAD_COUNT;
        queueList.get(index).offer(apiAuditVo);
    }
}
