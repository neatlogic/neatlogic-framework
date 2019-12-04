package codedriver.framework.restful.core;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

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

public class ApiAuditLog extends CodeDriverThread {
	
//	private final static Logger logger = LoggerFactory.getLogger(ApiAuditLog.class);

	private String uuid;
	private String param;
	private String error;
	private String result;
	private String tenantUuid;
	
	public ApiAuditLog() {
		
	}

	public ApiAuditLog(String uuid, String param, String error, String result, String tenantUuid) {
		this.uuid = uuid;
		this.param = param;
		this.error = error;
		this.result = result;
		this.tenantUuid = tenantUuid;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getTenantUuid() {
		return tenantUuid;
	}

	public void setTenantUuid(String tenantUuid) {
		this.tenantUuid = tenantUuid;
	}

	private String logFormat() {
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
		}
		if(StringUtils.isNotBlank(result)) {
			log.append("result: ");
			log.append(result);
			log.append("\n");
		}		
		return log.toString();
	}
	@Override
	protected void execute() {
//		if(tenantContext == null) {
//			tenantContext = TenantContext.init();
//		}
		Logger logger = getLogger(tenantUuid, ApiAuditLog.class);
		String oldThreadName = Thread.currentThread().getName();
		try {
			Thread.currentThread().setName("APIAUDITLOG");		
			logger.error(logFormat());
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}finally {
			Thread.currentThread().setName(oldThreadName);
		}		
	}

	public Logger getLogger(String tenantUuid, Class<?> clazz){  
        Logger logger = (Logger) LoggerFactory.getLogger(clazz);  
  
        LoggerContext loggerContext = logger.getLoggerContext();  
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();  
//        encoder.setContext(loggerContext);
//        encoder.setPattern("[%-5level]%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %logger{36}[%line]- %msg%n");  
//        encoder.start();  
//      TimeBasedRollingPolicy rollingPolicyBase = new TimeBasedRollingPolicy<>();  
//      rollingPolicyBase.setContext(loggerContext);  
//      rollingPolicyBase.setParent(appender);  
//      rollingPolicyBase.setFileNamePattern("D:\\CodeDriverDevelop\\CODEDRIVER_HOME\\" + tenantUuid + "\\logs\\api.%d{yyyy-MM-dd}.%i.log");  
//      SizeAndTimeBasedFNATP sizeAndTimeBasedFNATP = new SizeAndTimeBasedFNATP();  
//      sizeAndTimeBasedFNATP.setMaxFileSize("10MB");  
//      rollingPolicyBase.setTimeBasedFileNamingAndTriggeringPolicy(sizeAndTimeBasedFNATP);  
//      rollingPolicyBase.setMaxHistory(10);  
//      rollingPolicyBase.start();  

//      appender.setEncoder(encoder);
//    appender.setRollingPolicy(rollingPolicyBase);  
        
        
//        SizeBasedTriggeringPolicy sizeBasedTriggeringPolicy = new SizeBasedTriggeringPolicy("10MB");
//        sizeBasedTriggeringPolicy.start();
//        
//        PatternLayout patternLayout = new PatternLayout();
//        patternLayout.setPattern("[%-5level]%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %logger{36}[%line]- %msg%n");
//        patternLayout.setContext(loggerContext);
//        patternLayout.start();
//        
//        RollingFileAppender appender = new RollingFileAppender();
//        appender.setFile("D:\\CodeDriverDevelop\\CODEDRIVER_HOME\\" + tenantUuid + "\\logs\\api.log");
//        appender.setContext(loggerContext);
//        appender.setLayout(patternLayout);
//        appender.setTriggeringPolicy(sizeBasedTriggeringPolicy);
//        FixedWindowRollingPolicy fixedWindowRollingPolicy= new FixedWindowRollingPolicy();
//        fixedWindowRollingPolicy.setContext(loggerContext);
//        fixedWindowRollingPolicy.setParent(appender);
//        fixedWindowRollingPolicy.setFileNamePattern(tenantUuid + "logs\\api.log.%i");
//        fixedWindowRollingPolicy.setMinIndex(1);
//        fixedWindowRollingPolicy.setMaxIndex(10);
//        fixedWindowRollingPolicy.start();     
//        appender.setRollingPolicy(fixedWindowRollingPolicy); 
//       
//        appender.start();  
//  
//        AsyncAppender asyncAppender = new AsyncAppender();
//        asyncAppender.setContext(loggerContext);
//        asyncAppender.setDiscardingThreshold(0);
//        asyncAppender.setQueueSize(50);
//        asyncAppender.addAppender(appender);
//        asyncAppender.start();
//        logger.setAdditive(false);
//        logger.setLevel(Level.ERROR);
//        logger.addAppender(asyncAppender);  
  
      TriggeringPolicy sizeBasedTriggeringPolicy = new SizeBasedTriggeringPolicy("10MB");
      sizeBasedTriggeringPolicy.start();
      
      PatternLayoutBase patternLayout = new PatternLayout();
      patternLayout.setPattern("[%-5level]%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %logger{36}[%line]- %msg%n");
      patternLayout.setContext(loggerContext);
      patternLayout.start();
      
      RollingFileAppender appender = new RollingFileAppender();
      appender.setFile("D:\\CodeDriverDevelop\\CODEDRIVER_HOME\\" + tenantUuid + "\\logs\\api.log");
      appender.setContext(loggerContext);
      appender.setEncoder(encoder);
      appender.setLayout(patternLayout);
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

      AsyncAppender asyncAppender = new AsyncAppender();
      asyncAppender.setContext(loggerContext);
      asyncAppender.setDiscardingThreshold(0);
      asyncAppender.setQueueSize(50);
      asyncAppender.addAppender(appender);
      asyncAppender.start();
      logger.setAdditive(false);
      logger.setLevel(Level.ERROR);
      logger.addAppender(asyncAppender);
        
        return logger;  
    }
}
