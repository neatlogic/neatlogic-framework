package codedriver.framework.asynchronization.threadpool;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public class CachedThreadPool {
	private static final Log logger = LogFactory.getLog(CachedThreadPool.class);
	private static final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

	public static void execute(CodeDriverThread command) {
		try {
			cachedThreadPool.execute(command);
		} catch (RejectedExecutionException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

}
