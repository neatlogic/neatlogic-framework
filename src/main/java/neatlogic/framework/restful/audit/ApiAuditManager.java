/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
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
