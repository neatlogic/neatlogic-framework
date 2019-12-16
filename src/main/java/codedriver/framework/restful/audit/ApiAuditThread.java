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
import com.alibaba.fastjson.serializer.SerializerFeature;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.common.config.Config;
import codedriver.framework.restful.dto.ApiAuditVo;

public class ApiAuditThread extends CodeDriverThread {
	Logger logger = LoggerFactory.getLogger(ApiAuditThread.class);

	private BlockingQueue<ApiAuditVo> queue;
	private Map<String, OutputStreamWriter> apiAuditWriterMap = new HashMap<>();

	public ApiAuditThread(BlockingQueue<ApiAuditVo> _queue) {
		queue = _queue;
	}

	private String getLogPath(String tenant, String logPath) {
		String logFilePath = "";
		if (StringUtils.isBlank(logPath)) {
			logFilePath = Config.CODEDRIVER_HOME + File.separator + "apiaudit" + File.separator + tenant + File.separator + "audit.log";
		} else {
			if (logPath.startsWith(File.separator)) {
				logFilePath = logPath + File.separator + tenant + File.separator + "audit.log";
			} else {
				logFilePath = Config.CODEDRIVER_HOME + File.separator + logPath + File.separator + tenant + File.separator + "audit.log";
			}
		}
		return logFilePath;
	}

	public static String formatContent(ApiAuditVo apiAuditVo) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		StringBuilder log = new StringBuilder();
		log.append(">>>>>>>>>>>>>\nuuid: ");
		log.append(apiAuditVo.getUuid());
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
			String pretty = JSON.toJSONString(apiAuditVo.getResult(), SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
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
						File file = new File(logFilePath);
						if (!file.getParentFile().exists()) {
							file.getParentFile().mkdirs();
						}
						if (!file.exists()) {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
