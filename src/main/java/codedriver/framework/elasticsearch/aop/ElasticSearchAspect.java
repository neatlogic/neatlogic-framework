package codedriver.framework.elasticsearch.aop;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadpool.CachedThreadPool;
import codedriver.framework.common.RootComponent;
import codedriver.framework.elasticsearch.annotation.ElasticSearch;
import codedriver.framework.elasticsearch.core.ElasticSearchFactory;
import codedriver.framework.elasticsearch.core.IElasticSearchHandler;

@Aspect
@RootComponent
public class ElasticSearchAspect {
	private static final ThreadLocal<Map<String, List<Object>>> ARGS_MAP = new ThreadLocal<>();

	@After("@annotation(elasticSearch)")
	public void ActionCheck(JoinPoint point, ElasticSearch elasticSearch) {
		if (elasticSearch != null && StringUtils.isNotBlank(elasticSearch.type()) && ElasticSearchFactory.getHandler(elasticSearch.type()) != null) {
			if (!TransactionSynchronizationManager.isSynchronizationActive()) {
				List<Object> argList = Arrays.asList(point.getArgs());
				CachedThreadPool.execute(new ElasticSearchHandler(elasticSearch.type(), argList));
			} else {
				Map<String, List<Object>> argMap = ARGS_MAP.get();
				if (argMap == null) {
					argMap = new HashMap<>();
					ARGS_MAP.set(argMap);
					TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
						@Override
						public void afterCommit() {
							Map<String, List<Object>> argMap = ARGS_MAP.get();
							Iterator<String> keys = argMap.keySet().iterator();
							while (keys.hasNext()) {
								String key = keys.next();
								CachedThreadPool.execute(new ElasticSearchHandler(key, argMap.get(key)));
							}
						}

						@Override
						public void afterCompletion(int status) {
							ARGS_MAP.remove();
						}
					});
				}
				if (argMap.containsKey(elasticSearch.type())) {
					argMap.get(elasticSearch.type()).addAll(Arrays.asList(point.getArgs()));
				} else {
					argMap.put(elasticSearch.type(), Arrays.asList(point.getArgs()));
				}
			}
		}
	}

	private static class ElasticSearchHandler extends CodeDriverThread {
		private String handler;
		private List<Object> paramList;

		public ElasticSearchHandler(String _handler, List<Object> _paramList) {
			handler = _handler;
			paramList = _paramList;
		}

		@Override
		protected void execute() {
			Thread.currentThread().setName("ELASTICSEARCH-HANDLER-" + handler);
			IElasticSearchHandler eshandler = ElasticSearchFactory.getHandler(handler);
			if (eshandler != null) {
				eshandler.doService(paramList);
			}
		}

	}
}
