package codedriver.framework.restful.audit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import codedriver.framework.restful.dto.ApiAuditVo;

@Component
public class ApiAuditManager {
	public final static int THREAD_COUNT = 3;
	private final static int QUEUE_SIZE = 512;

	private static List<BlockingQueue<ApiAuditVo>> queueList = new ArrayList<>();

	@PostConstruct
	public void init() {
		for (int i = 0; i < THREAD_COUNT; i++) {
			BlockingQueue queue = new ArrayBlockingQueue<ApiAuditVo>(QUEUE_SIZE, true);
			queueList.add(queue);
			Thread thread = new Thread(new ApiAuditThread(queue), "API-AUDIT-" + i);
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
