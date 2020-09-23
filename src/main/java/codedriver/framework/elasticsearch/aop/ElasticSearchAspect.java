package codedriver.framework.elasticsearch.aop;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadpool.CachedThreadPool;
import codedriver.framework.common.RootComponent;
import codedriver.framework.elasticsearch.annotation.ElasticSearch;
import codedriver.framework.elasticsearch.core.ElasticSearchFactory;
import codedriver.framework.elasticsearch.core.IElasticSearchHandler;
import codedriver.framework.elasticsearch.dao.mapper.ElasticSearchMapper;
import codedriver.framework.elasticsearch.dto.ElasticSearchAuditVo;

@Aspect
@RootComponent
public class ElasticSearchAspect {
	private static final ThreadLocal<Map<String, List<Object>>> ARGS_MAP = new ThreadLocal<>();
	private static Logger logger = LoggerFactory.getLogger(ElasticSearchAspect.class);
	private static ElasticSearchMapper elasticSearchMapper;
	
	@Autowired
    private  void setReminderMapper(ElasticSearchMapper _elasticSearchMapper) {
		elasticSearchMapper = _elasticSearchMapper;
    }
	
	@After("@annotation(elasticSearch)")
	public void ActionCheck(JoinPoint point, ElasticSearch elasticSearch) {
		List<Object> argList = Arrays.asList(point.getArgs());
		argList = argList.stream().filter(object->object.getClass() == elasticSearch.paramType()).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(argList)&&elasticSearch != null && StringUtils.isNotBlank(elasticSearch.type()) && ElasticSearchFactory.getHandler(elasticSearch.type()) != null) {
			if (!TransactionSynchronizationManager.isSynchronizationActive()) {
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
					argMap.get(elasticSearch.type()).addAll(argList);
				} else {
					argMap.put(elasticSearch.type(), argList);
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
				try {
					JSONObject paramJson = eshandler.getConfig(paramList);
					ElasticSearchAuditVo elasticSeachAduitVo = new ElasticSearchAuditVo(handler,JSONObject.toJSONString(paramJson));
					elasticSearchMapper.insertElasticSearchAudit(elasticSeachAduitVo);
					eshandler.save(paramJson);
					elasticSearchMapper.deleteElasticSearchAuditById(elasticSeachAduitVo.getId());
				}catch(Exception ex) {
					logger.error(ex.getMessage(),ex);
				}
			}
		}

	}
}
