package codedriver.framework.restful.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.pattern.PatternLayoutBase;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.TriggeringPolicy;
import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.dao.mapper.ConfigMapper;
import codedriver.framework.dto.ConfigVo;

public class ApiAuditLogger {
	
	private final static String PATTERN = "[%-5level]%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %logger{36}[%line]- %msg%n";
	private final static String FILE_NAME_PATTERN_HOUR = "%d{yyyy-MM-dd_HH}.zip";
	private final static String FILE_NAME_PATTERN_DAY = "%d{yyyy-MM-dd}.zip";
	private final static String FILE_NAME_PATTERN_WEEK = "%d{yyyy-MM-dd}.zip";
	private final static String FILE_NAME_PATTERN_MONTH = "%d{yyyy-MM-dd}.zip";
	private final static String FILE_NAME_PATTERN_FIXED_SIZE = "%i.zip";
	@Autowired
	private ConfigMapper configMapper;
	
	private static Map<String, Logger> loggerMap = new HashMap<>();

	public void log(String uuid, JSONObject param, String error, Object result) {		
		Logger logger = getLogger();
		if(logger.isErrorEnabled()) {
			String log = logFormat(uuid, param, error, result);
			logger.error(log);
		}		
	}

	private static String logFormat(String uuid, JSONObject param, String error, Object result) {
		StringBuilder log = new StringBuilder();
		log.append("\n");
		log.append("uuid: ");
		log.append(uuid);
		log.append("\n");
		log.append("param: ");
		log.append(param);
		log.append("\n");
		if(StringUtils.isNotBlank(error)) {
			log.append("error: ");
			log.append(error);
			log.append("\n");
		}else {
			log.append("result: ");
			log.append(result);
			log.append("\n");
		}		
		return log.toString();
	}
	
	private Logger getLogger(){
		String tenantUuid = TenantContext.get().getTenantUuid();
		if(loggerMap.containsKey(tenantUuid)) {
			return loggerMap.get(tenantUuid);
		}
		ConfigVo config = configMapper.getConfigByKey("");
		JSONObject json = new JSONObject();
		if(config != null) {
			String value = config.getValue();
			json = JSONObject.parseObject(value);
		}
		if(json == null) {
			json = new JSONObject();
		}
		Logger logger = LoggerBuilder(json);
		loggerMap.put(tenantUuid, logger);
		return logger;
         
    }

	private Logger LoggerBuilder(JSONObject json) {
		String rollingPolicy = json.getString("rollingPolicy");
		String file = json.getString("file");
		Integer maxFileSize = json.getInteger("maxFileSize");
		Integer maxHistory = json.getInteger("maxHistory");
		Integer queueSize = json.getInteger("queueSize");
		String tenantUuid = TenantContext.get().getTenantUuid();
		Logger logger = (Logger) LoggerFactory.getLogger(ApiAuditLogger.class.getName() + "-" + tenantUuid);  		  
        LoggerContext loggerContext = logger.getLoggerContext();  
        //日志格式设置
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();  
        encoder.setContext(loggerContext);
        encoder.setPattern(PATTERN);  
        encoder.start();    
        //单个文件大小
        TriggeringPolicy sizeBasedTriggeringPolicy = new SizeBasedTriggeringPolicy("10MB");
        sizeBasedTriggeringPolicy.start();
        //日志输出方式设置
        RollingFileAppender appender = new RollingFileAppender();
        appender.setFile("D:\\CodeDriverDevelop\\CODEDRIVER_HOME\\" + tenantUuid + "\\logs\\api.log");
        appender.setContext(loggerContext);
        appender.setEncoder(encoder);
        appender.setTriggeringPolicy(sizeBasedTriggeringPolicy);
        
        FixedWindowRollingPolicy fixedWindowRollingPolicy= new FixedWindowRollingPolicy();
        fixedWindowRollingPolicy.setContext(loggerContext);
        fixedWindowRollingPolicy.setParent(appender);
        fixedWindowRollingPolicy.setFileNamePattern(tenantUuid + "logs\\api.log.%i");
        fixedWindowRollingPolicy.setMinIndex(1);
        fixedWindowRollingPolicy.setMaxIndex(10);
        fixedWindowRollingPolicy.start();     
        appender.setRollingPolicy(fixedWindowRollingPolicy);      
        appender.start();  

        //异步
        AsyncAppender asyncAppender = new AsyncAppender();
        asyncAppender.setContext(loggerContext);
        asyncAppender.setDiscardingThreshold(0);
        asyncAppender.setQueueSize(queueSize);
        asyncAppender.addAppender(appender);
        asyncAppender.start();
        logger.setAdditive(false);
        logger.setLevel(Level.ERROR);
        logger.addAppender(asyncAppender);
        
        return logger; 
	}
}
