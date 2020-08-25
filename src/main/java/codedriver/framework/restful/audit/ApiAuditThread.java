package codedriver.framework.restful.audit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.common.config.Config;
import codedriver.framework.restful.dto.ApiAuditVo;

public class ApiAuditThread extends CodeDriverThread {
	Logger logger = LoggerFactory.getLogger(ApiAuditThread.class);
	private static long maxFileSize = 1024 * 1024 * 10;
	private BlockingQueue<ApiAuditVo> queue;
	private Map<String, OutputStreamWriter> apiAuditWriterMap = new HashMap<>();
	private Map<String, String> fileNameMap = new HashMap<>();

	public ApiAuditThread(BlockingQueue<ApiAuditVo> _queue) {
		queue = _queue;
	}

	private String getLogPath(String tenant, String logPath) {
		String logFilePath = "";
		if (StringUtils.isBlank(logPath)) {
			logFilePath = Config.DATA_HOME() + File.separator + "apiaudit" + File.separator + tenant + File.separator;
		} else {
			if (logPath.startsWith(File.separator)) {
				logFilePath = logPath + File.separator + tenant + File.separator;
			} else {
				logFilePath = Config.DATA_HOME() + File.separator + logPath + File.separator + tenant + File.separator;
			}
		}
		return logFilePath;
	}

	private String getFileName(String tenant) {
		if (!fileNameMap.containsKey(tenant)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
			String filename = "audit.log." + sdf.format(new Date());
			fileNameMap.put(tenant, filename);
		}
		return fileNameMap.get(tenant);
	}

	private String getRotateFileName(String tenant) {
		String filename = getFileName(tenant);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		filename = filename + "-" + sdf.format(new Date());
		fileNameMap.remove(tenant);
		return filename;
	}

	public static String formatContent(ApiAuditVo apiAuditVo) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		StringBuilder log = new StringBuilder();
		log.append(">>>>>>>>>>>>>\nid: ");
		log.append(apiAuditVo.getId());
		log.append("\n");
		log.append("user: ");
		log.append(apiAuditVo.getUserUuid());
		log.append("\n");
		log.append("time: ");
		log.append(sdf.format(new Date()));
		log.append("\n");
		if (StringUtils.isNotBlank(apiAuditVo.getParam())) {
			log.append("param: ");
			log.append(apiAuditVo.getParam());
			log.append("\n");
		}
		if (apiAuditVo.getResult() != null) {
			log.append("result: ");
			String pretty = JSON.toJSONString(apiAuditVo.getResult());
			log.append(pretty);
			log.append("\n");
		}
		if (StringUtils.isNotBlank(apiAuditVo.getError())) {
			log.append("error: ");
			log.append(apiAuditVo.getError());
			log.append("\n");
		}
		log.append("<<<<<<<<<<<<<\n\n");
		return log.toString();
	}

	@Override
	public void execute() {
		ApiAuditVo apiAuditVo = null;
		String lastTenantUuid = "";
		try {
			while ((apiAuditVo = queue.take()) != null) {
				String oldThreadName = Thread.currentThread().getName();
				try {
					Thread.currentThread().setName("API-AUDIT-" + apiAuditVo.getTenant() + "-" + apiAuditVo.getToken());
					if (StringUtils.isNotBlank(lastTenantUuid) && !lastTenantUuid.equals(apiAuditVo.getTenant())) {
						OutputStreamWriter writer = apiAuditWriterMap.get(lastTenantUuid);
						if (writer != null) {
							writer.flush();
							writer.close();
							writer = null;
							apiAuditWriterMap.remove(lastTenantUuid);
						}
					}

					OutputStreamWriter writer = apiAuditWriterMap.get(apiAuditVo.getTenant());
					if (writer == null) {
						String logFilePath = getLogPath(apiAuditVo.getTenant(), apiAuditVo.getLogPath());
						File file = new File(logFilePath + getFileName(apiAuditVo.getTenant()));
						if (file.exists()) {
							if (file.length() > maxFileSize) {
								file.renameTo(new File(logFilePath + getRotateFileName(apiAuditVo.getTenant())));
								file = new File(logFilePath + getFileName(apiAuditVo.getTenant()));
								file.createNewFile();
							}
						} else {
							if (!file.getParentFile().exists()) {
								file.getParentFile().mkdirs();
							}
							file.createNewFile();
						}
						writer = new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8");
						apiAuditWriterMap.put(apiAuditVo.getTenant(), writer);
					}
					if (writer != null) {
						writer.write(formatContent(apiAuditVo));
					}
					if (queue.size() == 0) {
						if (writer != null) {
							writer.flush();
							writer.close();
							writer = null;
							apiAuditWriterMap.remove(apiAuditVo.getTenant());
						}
					}
					lastTenantUuid = apiAuditVo.getTenant();
				} catch (Exception ex) {
					logger.error(ex.getMessage(), ex);
				} finally {
					Thread.currentThread().setName(oldThreadName);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
