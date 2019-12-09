package codedriver.framework.restful.core;

import java.io.File;
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
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.RollingPolicy;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.RootComponent;
import codedriver.framework.common.config.Config;
import codedriver.framework.dao.mapper.ConfigMapper;
import codedriver.framework.dto.ConfigVo;
@RootComponent
public class ApiAuditLogger {
	private org.slf4j.Logger logger = LoggerFactory.getLogger(ApiAuditLogger.class);
	
	private final static String API_LOG_CONFIG = "api_log_config";//接口访问日志配置在config表的key值
	private final static String PATTERN = "[%-5level]%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %logger{36}[%line]- %msg%n";//日志格式
	private final static String FILE_SIZE_UNIT = "MB"; //日志文件大小单位
	private final static String DEFAULT_ROLLING_POLICY = "fixedSize";//默认轮转策略
	private final static String DEFAULT_FILE = "/logs/api.log";//默认文件路径
	private final static String DEFAULT_MAX_FILE_SIZE = "10MB";//默认单个文件大小
	private final static int DEFAULT_MAX_HISTORY = 10;//默认保留10个文件
	private final static int DEFAULT_QUEUE_SIZE = 256;//默认异步输出日志的阻塞队列大小256个
	
	private static Map<String, Logger> loggerMap = new HashMap<>();
	private static Map<String, String> fileNamePatternMap = new HashMap<>();
	
	static {
		fileNamePatternMap.put("minute",".%d{yyyy-MM-dd HH-mm}.%i");
		fileNamePatternMap.put("hour",".%d{yyyy-MM-dd HH}.%i");
		fileNamePatternMap.put("day",".%d{yyyy-MM-dd}.%i");
		fileNamePatternMap.put("week",".%d{yyyy-WW}.%i");
		fileNamePatternMap.put("month",".%d{yyyy-MM}.%i");
		fileNamePatternMap.put("fixedSize",".%i");
	}

	@Autowired
	private ConfigMapper configMapper;
	/**
	 * 
	* @Description: 异步打印日志 
	* @param uuid 对应api_audit表的uuid
	* @param param 请求参数
	* @param error 处理异常信息
	* @param result 请求返回结果 
	* @return void
	 */
	public void log(String uuid, JSONObject param, String error, Object result) {		
		Logger logger = getLogger();
		if(logger.isErrorEnabled()) {
			String log = logFormat(uuid, param, error, result);
			logger.error(log);
		}		
	}
	/**
	 * 
	* @Description: 接口日志格式化
	* @param uuid 对应api_audit表的uuid
	* @param param 请求参数
	* @param error 处理异常信息
	* @param result 请求返回结果
	* @return String
	 */
	private String logFormat(String uuid, JSONObject param, String error, Object result) {
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
	/**
	 * 
	* @Description: 获取当前租户的接口日志logger 
	* @return Logger
	 */
	private Logger getLogger(){
		String tenantUuid = TenantContext.get().getTenantUuid();
		if(loggerMap.containsKey(tenantUuid)) {
			return loggerMap.get(tenantUuid);
		}
		String rollingPolicy = DEFAULT_ROLLING_POLICY;
		String file = DEFAULT_FILE;
		String maxFileSize = DEFAULT_MAX_FILE_SIZE;
		int maxHistory = DEFAULT_MAX_HISTORY;
		int queueSize = DEFAULT_QUEUE_SIZE;
		ConfigVo config = configMapper.getConfigByKey(API_LOG_CONFIG);
		JSONObject json = new JSONObject();
		if(config != null) {
			String value = config.getValue();
			try {
				json = JSONObject.parseObject(value);
				if(json != null) {
					if(json.containsKey("rollingPolicy")) {
						String customRollingPolicy = json.getString("rollingPolicy");
						if(fileNamePatternMap.containsKey(customRollingPolicy)) {
							rollingPolicy = customRollingPolicy;
						}
					}		
					if(json.containsKey("file")) {
						String customFile = json.getString("file");
						if(StringUtils.isNotBlank(customFile)) {
							file = customFile.startsWith(File.separator) ? customFile : File.separator + customFile;
						}
					}		
					if(json.containsKey("maxFileSize")) {
						try {
							maxFileSize = json.getIntValue("maxFileSize") + FILE_SIZE_UNIT;
						}catch(NumberFormatException e) {
							logger.error(e.getMessage(), e);
						}						
					}		
					if(json.containsKey("maxHistory")) {
						try {
							maxHistory = json.getIntValue("maxHistory");
						}catch(NumberFormatException e) {
							logger.error(e.getMessage(), e);
						}
					}		
					if(json.containsKey("queueSize")) {
						try {
							queueSize = json.getIntValue("queueSize");
						}catch(NumberFormatException e) {
							logger.error(e.getMessage(), e);
						}
					}
				}			
			}catch(Exception e) {
				logger.error(e.getMessage(), e);
			}			
		}
		Logger logger = LoggerBuilder(file, rollingPolicy, maxFileSize, maxHistory, queueSize);
		loggerMap.put(tenantUuid, logger);
		return logger;
         
    }
	/**
	 * 
	* @Description: TODO 
	* @param file 文件路径
	* @param rollingPolicy 轮转策略
	* @param maxFileSize 单个文件大小
	* @param maxHistory 保留文件个数
	* @param queueSize 异步输出日志的阻塞队列大小
	* @return Logger
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Logger LoggerBuilder(String file, String rollingPolicy, String maxFileSize, int maxHistory, int queueSize) {
		String tenantUuid = TenantContext.get().getTenantUuid();
		file = Config.REST_AUDIT_PATH + tenantUuid + file;
		Logger logger = (Logger) LoggerFactory.getLogger(ApiAuditLogger.class.getName() + "-" + tenantUuid);  		  
        LoggerContext loggerContext = logger.getLoggerContext();  
        //日志格式设置
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();  
        encoder.setContext(loggerContext);
        encoder.setPattern(PATTERN);  
        encoder.start();    
        
        //日志输出方式设置
        RollingFileAppender appender = new RollingFileAppender();
        appender.setFile(file);
        appender.setContext(loggerContext);
        appender.setEncoder(encoder);
        
        RollingPolicy rollingPolicyBase = null;
        String fileNamePattern = file + fileNamePatternMap.get(rollingPolicy);
        if(rollingPolicy.equals(DEFAULT_ROLLING_POLICY)) {
        	//单个文件大小
        	SizeBasedTriggeringPolicy sizeBasedTriggeringPolicy = new SizeBasedTriggeringPolicy();       	
        	sizeBasedTriggeringPolicy.setMaxFileSize(maxFileSize);
            sizeBasedTriggeringPolicy.start();
            appender.setTriggeringPolicy(sizeBasedTriggeringPolicy);
            //固定窗口固定大小轮转策略
        	FixedWindowRollingPolicy fixedWindowRollingPolicy= new FixedWindowRollingPolicy();
            fixedWindowRollingPolicy.setContext(loggerContext);
            fixedWindowRollingPolicy.setParent(appender);
            fixedWindowRollingPolicy.setFileNamePattern(fileNamePattern);
            fixedWindowRollingPolicy.setMinIndex(1);
            fixedWindowRollingPolicy.setMaxIndex(maxHistory);
            fixedWindowRollingPolicy.start();
            rollingPolicyBase = fixedWindowRollingPolicy;
        }else {
        	//大小以及时间轮转策略
        	SizeAndTimeBasedRollingPolicy sizeAndTimeBasedRollingPolicy = new SizeAndTimeBasedRollingPolicy();
        	sizeAndTimeBasedRollingPolicy.setContext(loggerContext);
        	sizeAndTimeBasedRollingPolicy.setParent(appender);
        	sizeAndTimeBasedRollingPolicy.setFileNamePattern(fileNamePattern);
        	sizeAndTimeBasedRollingPolicy.setMaxFileSize(maxFileSize);
        	sizeAndTimeBasedRollingPolicy.setMaxHistory(maxHistory);
        	sizeAndTimeBasedRollingPolicy.start();
        	rollingPolicyBase = sizeAndTimeBasedRollingPolicy;
        }
             
        appender.setRollingPolicy(rollingPolicyBase);      
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
	
//	public static void main(String[] args) {
//		ApiAuditLogger apiAuditLogger = new ApiAuditLogger();
//		
//		String uuid = null;
//		JSONObject json = new JSONObject();
//		StringBuilder error = new StringBuilder();
//		int count = 10000;
//		for(int i = 0; i < count; i++) {
//			error.append("error-");
//		}
//		StringBuilder result = new StringBuilder();
//		for(int i = 0; i < count; i++) {
//			result.append("result-");
//		}
//		uuid = UUID.randomUUID().toString().replace("-", "");
//		json.put("uuid", uuid);
//		apiAuditLogger.log(uuid, json, error.toString(), result);
//		
//		for(int j = 0; j < 130; j++) {
//			long startTime = System.currentTimeMillis();
//			for(int i = 0; i < DEFAULT_QUEUE_SIZE; i++) {
//				uuid = UUID.randomUUID().toString().replace("-", "");
//				json.put("uuid", uuid);
//				apiAuditLogger.log(uuid, json, error.toString(), result);
//				try {
//					Thread.sleep(50);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//			long cost = System.currentTimeMillis() - startTime;
//			System.out.println(cost);
//			try {
//				Thread.sleep(61000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}		
//	}

}
