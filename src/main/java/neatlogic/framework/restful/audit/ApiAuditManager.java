/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.restful.audit;

import neatlogic.framework.restful.dto.ApiAuditVo;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Component
public class ApiAuditManager {
    public final static int THREAD_COUNT = 3;
    private final static int QUEUE_SIZE = 512;

    private static List<BlockingQueue<ApiAuditVo>> queueList = new ArrayList<>();

    @PostConstruct
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
