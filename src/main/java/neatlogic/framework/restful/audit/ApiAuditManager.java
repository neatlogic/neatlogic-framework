/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

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
