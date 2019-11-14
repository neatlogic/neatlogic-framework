package codedriver.framework.asynchronization.threadpool;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CommonThreadPool {
	private static final Log logger = LogFactory.getLog(CommonThreadPool.class);
	private static BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
	private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10, 30, TimeUnit.SECONDS, workQueue, new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setDaemon(true);
			t.setName("COMMON-THREDPOOL-" + t.getName());
			return t;
		}
	}, new ThreadPoolExecutor.AbortPolicy());

	public static void execute(Runnable command) {
		try {
			threadPoolExecutor.execute(command);
		} catch (RejectedExecutionException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	public static ThreadPoolExecutor getThreadPool() {
		return threadPoolExecutor;
	}

	public static void invokeAll(List<Callable<Object>> commandList) {
		try {
			threadPoolExecutor.invokeAll(commandList);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
