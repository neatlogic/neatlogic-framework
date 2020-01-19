package codedriver.framework.asynchronization.threadpool;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import codedriver.framework.asynchronization.thread.CodeDriverThread;

public class CachedThreadPool {
	private static final Log logger = LogFactory.getLog(CachedThreadPool.class);
	private static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

	public static void execute(CodeDriverThread command) {
		try {
			cachedThreadPool.execute(command);
		} catch (RejectedExecutionException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

}
