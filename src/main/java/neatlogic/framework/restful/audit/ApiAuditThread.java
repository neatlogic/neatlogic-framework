/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.restful.audit;

import com.alibaba.fastjson.JSON;
import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.restful.dto.ApiAuditVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class ApiAuditThread extends NeatLogicThread {
    Logger logger = LoggerFactory.getLogger(ApiAuditThread.class);
    private static final long maxFileSize = 1024 * 1024 * 10;
    private BlockingQueue<ApiAuditVo> queue;
    private Map<String, OutputStreamWriter> apiAuditWriterMap = new HashMap<>();
    private Map<String, String> fileNameMap = new HashMap<>();

    public ApiAuditThread(BlockingQueue<ApiAuditVo> _queue, String threadName) {
        super(threadName);
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
