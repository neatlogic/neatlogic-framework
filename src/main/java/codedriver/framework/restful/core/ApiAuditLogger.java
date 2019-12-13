package codedriver.framework.restful.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.RollingPolicy;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.config.Config;
import codedriver.framework.dao.mapper.ConfigMapper;
import codedriver.framework.dto.ConfigVo;
import codedriver.framework.restful.dto.ApiAuditContentVo;

@Component
public class ApiAuditLogger {
	private org.slf4j.Logger logger = LoggerFactory.getLogger(ApiAuditLogger.class);

	private final static String API_LOG_CONFIG = "api_log_config";// 接口访问日志配置在config表的key值
	private final static String PATTERN = "%msg%n";// 日志格式
	private final static String FILE_SIZE_UNIT = "MB"; // 日志文件大小单位
	private final static String DEFAULT_ROLLING_POLICY = "fixedSize";// 默认轮转策略
	private final static String DEFAULT_FILE = "/logs/api.log";// 默认文件路径
	private final static String DEFAULT_MAX_FILE_SIZE = "10MB";// 默认单个文件大小
	private final static int DEFAULT_MAX_HISTORY = 20;// 固定大小轮转策略默认保留20个历史文件，时间及大小的轮转策略默认保留全部历史文件

	public final static int THREAD_COUNT = 3;
	private final static int QUEUE_SIZE = 512;
	private static Map<String, Logger> loggerMap = new HashMap<>();
	private static Map<String, String> fileNamePatternMap = new HashMap<>();

	private static List<ArrayBlockingQueue<ApiAuditContentVo>> queueList = new ArrayList<>();

	static {
		fileNamePatternMap.put("minute", ".%d{yyyy-MM-dd HH-mm}.%i");
		fileNamePatternMap.put("hour", ".%d{yyyy-MM-dd HH}.%i");
		fileNamePatternMap.put("day", ".%d{yyyy-MM-dd}.%i");
		fileNamePatternMap.put("week", ".%d{yyyy-WW}.%i");
		fileNamePatternMap.put("month", ".%d{yyyy-MM}.%i");
		fileNamePatternMap.put("fixedSize", ".%i");
		for (int i = 0; i < THREAD_COUNT; i++) {
			queueList.add(new ArrayBlockingQueue<ApiAuditContentVo>(QUEUE_SIZE, false));
		}
	}

	public synchronized static ArrayBlockingQueue<ApiAuditContentVo> getQueue(int index) {
		return queueList.get(index);
	}

	@Autowired
	private ConfigMapper configMapper;

	@PostConstruct
	public void init() {
		for (int i = 0; i < THREAD_COUNT; i++) {
			Thread thread = new Thread(new ApiAuditTask(), "API-AUDIT-LOGGER-THREAD-" + i);
			thread.setDaemon(true);
			thread.start();
		}
	}

	/**
	 * 
	 * @Description: 获取当前租户的接口日志logger
	 * @return Logger
	 */
	private Logger getLogger(String tenantUuid) {
		// 切换租户库
		TenantContext tenantContext = TenantContext.get();
		if (tenantContext == null) {
			tenantContext.init(tenantUuid);
		} else {
			tenantContext.setTenantUuid(tenantUuid);
		}
		if (loggerMap.containsKey(tenantUuid)) {
			return loggerMap.get(tenantUuid);
		}
		String rollingPolicy = DEFAULT_ROLLING_POLICY;
		String file = DEFAULT_FILE;
		String maxFileSize = DEFAULT_MAX_FILE_SIZE;
		int maxHistory = DEFAULT_MAX_HISTORY;

		ConfigVo config = configMapper.getConfigByKey(API_LOG_CONFIG);
		JSONObject json = new JSONObject();
		if (config != null) {
			String value = config.getValue();
			try {
				json = JSONObject.parseObject(value);
				if (json != null) {
					if (json.containsKey("rollingPolicy")) {
						String customRollingPolicy = json.getString("rollingPolicy");
						if (fileNamePatternMap.containsKey(customRollingPolicy)) {
							rollingPolicy = customRollingPolicy;
						}
					}
					if (json.containsKey("file")) {
						String customFile = json.getString("file");
						if (StringUtils.isNotBlank(customFile)) {
							file = customFile.startsWith(File.separator) ? customFile : File.separator + customFile;
						}
					}
					if (json.containsKey("maxFileSize")) {
						try {
							maxFileSize = json.getIntValue("maxFileSize") + FILE_SIZE_UNIT;
						} catch (NumberFormatException e) {
							logger.error(e.getMessage(), e);
						}
					}
					if (json.containsKey("maxHistory")) {
						try {
							maxHistory = json.getIntValue("maxHistory");
						} catch (NumberFormatException e) {
							if (!rollingPolicy.equals(DEFAULT_ROLLING_POLICY)) {
								maxHistory = 0;
							}
							logger.error(e.getMessage(), e);
						}
					} else {
						if (!rollingPolicy.equals(DEFAULT_ROLLING_POLICY)) {
							maxHistory = 0;
						}
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		Logger logger = LoggerBuilder(tenantUuid, file, rollingPolicy, maxFileSize, maxHistory);
		loggerMap.put(tenantUuid, logger);
		return logger;

	}

	/**
	 * 
	 * @Description: TODO
	 * @param file
	 *            文件路径
	 * @param rollingPolicy
	 *            轮转策略
	 * @param maxFileSize
	 *            单个文件大小
	 * @param maxHistory
	 *            保留文件个数
	 * @param queueSize
	 *            异步输出日志的阻塞队列大小
	 * @return Logger
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Logger LoggerBuilder(String tenantUuid, String file, String rollingPolicy, String maxFileSize, int maxHistory) {
		if (!file.startsWith(File.separator)) {
			if (Config.CODEDRIVER_HOME.endsWith(File.separator)) {
				file = Config.CODEDRIVER_HOME + tenantUuid + File.separator + file;
			} else {
				file = Config.CODEDRIVER_HOME + File.separator + tenantUuid + File.separator + file;
			}
		}

		Logger logger = (Logger) LoggerFactory.getLogger(ApiAuditLogger.class.getName() + "-" + tenantUuid);
		LoggerContext loggerContext = logger.getLoggerContext();
		// 日志格式设置
		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		encoder.setContext(loggerContext);
		encoder.setPattern(PATTERN);
		encoder.start();

		// 日志输出方式设置
		RollingFileAppender appender = new RollingFileAppender();
		appender.setFile(file);
		appender.setContext(loggerContext);
		appender.setEncoder(encoder);

		RollingPolicy rollingPolicyBase = null;
		String fileNamePattern = file + fileNamePatternMap.get(rollingPolicy);
		if (rollingPolicy.equals(DEFAULT_ROLLING_POLICY)) {
			// 单个文件大小
			SizeBasedTriggeringPolicy sizeBasedTriggeringPolicy = new SizeBasedTriggeringPolicy();
			sizeBasedTriggeringPolicy.setMaxFileSize(maxFileSize);
			sizeBasedTriggeringPolicy.start();
			appender.setTriggeringPolicy(sizeBasedTriggeringPolicy);
			// 固定窗口固定大小轮转策略
			FixedWindowRollingPolicy fixedWindowRollingPolicy = new FixedWindowRollingPolicy();
			fixedWindowRollingPolicy.setContext(loggerContext);
			fixedWindowRollingPolicy.setParent(appender);
			fixedWindowRollingPolicy.setFileNamePattern(fileNamePattern);
			fixedWindowRollingPolicy.setMinIndex(1);
			fixedWindowRollingPolicy.setMaxIndex(maxHistory);
			fixedWindowRollingPolicy.start();
			rollingPolicyBase = fixedWindowRollingPolicy;
		} else {
			// 大小以及时间轮转策略
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

		logger.setAdditive(false);
		logger.setLevel(Level.TRACE);
		logger.addAppender(appender);
		return logger;
	}

	public class ApiAuditTask extends CodeDriverThread {

		@Override
		public void execute() {
			try {
				ApiAuditContentVo apiAuditContent = null;
				String tenentUuid = null;
				String oldName = Thread.currentThread().getName();
				int index = Integer.parseInt(oldName.substring(oldName.length() - 1));
				ArrayBlockingQueue<ApiAuditContentVo> queue = queueList.get(index);
				while ((apiAuditContent = queue.take()) != null) {
					tenentUuid = apiAuditContent.getTenantUuid();
					Logger logger = getLogger(tenentUuid);
					if (logger.isTraceEnabled()) {
						String log = apiAuditContent.logFormat();
						logger.trace(log);
					}
				}
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

}
