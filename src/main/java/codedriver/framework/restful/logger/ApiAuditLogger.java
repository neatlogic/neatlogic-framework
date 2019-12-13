package codedriver.framework.restful.logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.config.Config;
import codedriver.framework.common.util.FileUtil;
import codedriver.framework.dao.mapper.ConfigMapper;
import codedriver.framework.dto.ConfigVo;

@Component
public class ApiAuditLogger {
	private org.slf4j.Logger logger = LoggerFactory.getLogger(ApiAuditLogger.class);

	private final static String API_LOG_CONFIG = "api_log_config";// 接口访问日志配置在config表的key值
	private final static long FILE_SIZE_UNIT = 1024 * 1024; // 日志文件大小单位MB
	private final static String DEFAULT_FILE = "logs/api.log";// 默认文件路径
	private final static long DEFAULT_MAX_FILE_SIZE = 1 * FILE_SIZE_UNIT;// 默认单个文件大小
	private final static int DEFAULT_MAX_HISTORY = -1;//默认保留全部历史文件

	public final static int THREAD_COUNT = 3;
	private final static int QUEUE_SIZE = 512;
	private static Map<String, ApiAuditAppender> AppenderMap = new WeakHashMap<>();

	private static List<ArrayBlockingQueue<ApiAuditContent>> queueList = new ArrayList<>();

	static {
		for (int i = 0; i < THREAD_COUNT; i++) {
			queueList.add(new ArrayBlockingQueue<ApiAuditContent>(QUEUE_SIZE, false));
		}
	}

	public synchronized static ArrayBlockingQueue<ApiAuditContent> getQueue(int index) {
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

	private ApiAuditAppender getAppender(String tenantUuid) {
		ApiAuditAppender appender = AppenderMap.get(tenantUuid);
		if (appender != null) {
			return appender;
		}
		String file = DEFAULT_FILE;
		long maxFileSize = DEFAULT_MAX_FILE_SIZE;
		int maxHistory = DEFAULT_MAX_HISTORY;
		// 切换租户库
		TenantContext tenantContext = TenantContext.get();
		if (tenantContext == null) {
			TenantContext.init(tenantUuid);
		} else {
			tenantContext.setTenantUuid(tenantUuid);
		}
		ConfigVo config = configMapper.getConfigByKey(API_LOG_CONFIG);
		JSONObject json = new JSONObject();
		if (config != null) {
			String value = config.getValue();
			try {
				json = JSONObject.parseObject(value);
				if (json != null) {
					if (json.containsKey("file")) {
						String customFile = json.getString("file");
						if (StringUtils.isNotBlank(customFile)) {
							file = customFile;
						}
					}
					if (json.containsKey("maxFileSize")) {
						try {
							maxFileSize = json.getIntValue("maxFileSize") * FILE_SIZE_UNIT;
						} catch (NumberFormatException e) {
							logger.error(e.getMessage(), e);
						}
					}
					if (json.containsKey("maxHistory")) {
						try {
							maxHistory = json.getIntValue("maxHistory");
						} catch (NumberFormatException e) {
							logger.error(e.getMessage(), e);
						}
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		if (!file.startsWith(File.separator)) {
			if (Config.CODEDRIVER_HOME.endsWith(File.separator)) {
				file = Config.CODEDRIVER_HOME + tenantUuid + File.separator + file;
			} else {
				file = Config.CODEDRIVER_HOME + File.separator + tenantUuid + File.separator + file;
			}
		}
		ApiAuditAppender newAppender = new ApiAuditAppender(file, maxFileSize, maxHistory);
		AppenderMap.put(new String(tenantUuid), newAppender);
		return newAppender;
	}


	public class ApiAuditTask extends CodeDriverThread {

		@Override
		public void execute() {
			try {
				ApiAuditContent apiAuditContent = null;
				String tenentUuid = null;
				String oldName = Thread.currentThread().getName();
				int index = Integer.parseInt(oldName.substring(oldName.length() - 1));
				ArrayBlockingQueue<ApiAuditContent> queue = queueList.get(index);
				while ((apiAuditContent = queue.take()) != null) {
					tenentUuid = apiAuditContent.getTenantUuid();
					ApiAuditAppender apiAuditAppenderVo = getAppender(tenentUuid);
					FileUtil.writeContent(apiAuditContent, true, apiAuditAppenderVo);
				}
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

}
