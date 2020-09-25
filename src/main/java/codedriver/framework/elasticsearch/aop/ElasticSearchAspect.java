package codedriver.framework.elasticsearch.aop;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
import codedriver.framework.elasticsearch.annotation.ElasticSearchKey;
import codedriver.framework.elasticsearch.core.ElasticSearchFactory;
import codedriver.framework.elasticsearch.core.IElasticSearchHandler;
import codedriver.framework.elasticsearch.dao.mapper.ElasticSearchMapper;
import codedriver.framework.elasticsearch.dto.ElasticSearchAuditVo;

@Aspect
@RootComponent
public class ElasticSearchAspect {
	private static final ThreadLocal<Map<String, JSONObject>> ARGS_MAP = new ThreadLocal<>();
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
		if (CollectionUtils.isNotEmpty(argList) && StringUtils.isNotBlank(elasticSearch.type()) && ElasticSearchFactory.getHandler(elasticSearch.type()) != null) {
		    //拼接参数
		    JSONObject result = new JSONObject();
		    List<String> pkList = new ArrayList<String>();
            Object obj = argList.get(0);
            Field[] fields = obj.getClass().getDeclaredFields();
            for(Field field : fields) {
                ElasticSearchKey keyAnnota = field.getAnnotation(ElasticSearchKey.class);
                if(keyAnnota == null) {
                    continue;
                }
                try {
                    String fieldName = field.getName();
                    Object valueObj = obj.getClass().getMethod("get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1)).invoke(obj);
                    if(valueObj == null) {
                        continue;
                    }
                    String value = valueObj.toString();
                    String key = keyAnnota.id();
                    if("pk".equals(keyAnnota.type())) {
                        pkList.add(value);
                        
                    }else if(StringUtils.isNotBlank(key)){
                        key = fieldName;
                    }
                    result.put(key, value);
                } catch (IllegalArgumentException | IllegalAccessException|InvocationTargetException | NoSuchMethodException | SecurityException e) {
                    logger.error(e.getMessage(),e);
                }
            }
            //
			if (!TransactionSynchronizationManager.isSynchronizationActive()) {
				CachedThreadPool.execute(new ElasticSearchHandler(elasticSearch.type(), result));
			} else {
				Map<String, JSONObject> argMap = ARGS_MAP.get();
				if (argMap == null) {
					argMap = new HashMap<>();
					ARGS_MAP.set(argMap);
					TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
						@Override
						public void afterCommit() {
							Map<String, JSONObject> argMap = ARGS_MAP.get();
							Iterator<String> keys = argMap.keySet().iterator();
							while (keys.hasNext()) {
								String key = keys.next();
								CachedThreadPool.execute(new ElasticSearchHandler(elasticSearch.type(), argMap.get(key)));
							}
						}

						@Override
						public void afterCompletion(int status) {
							ARGS_MAP.remove();
						}
					});
				}
			    String pks = String.join("_", pkList);
                argMap.put(pks, result);
			}
		}
	}
	
	private static class ElasticSearchHandler extends CodeDriverThread {
		private String handler;
		private JSONObject paramJson;

		public ElasticSearchHandler(String _handler, JSONObject _paramJson) {
			handler = _handler;
			paramJson = _paramJson;
		}

		@Override
		protected void execute() {
			Thread.currentThread().setName("ELASTICSEARCH-HANDLER-" + handler);
			IElasticSearchHandler eshandler = ElasticSearchFactory.getHandler(handler);
			if (eshandler != null) {
				try {
				    String param = JSONObject.toJSONString(paramJson);
					ElasticSearchAuditVo elasticSeachAduitVo = new ElasticSearchAuditVo(handler,param);
					elasticSearchMapper.insertElasticSearchAudit(elasticSeachAduitVo);
					elasticSearchMapper.insertElasticSearchParam(elasticSeachAduitVo);
					eshandler.save(paramJson);
					elasticSearchMapper.deleteElasticSearchAudit(elasticSeachAduitVo);
				}catch(Exception ex) {
					logger.error(ex.getMessage(),ex);
				}
			}
		}

	}
}
