package codedriver.framework.scheduler.dto;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.annotation.JSONField;

import codedriver.framework.common.dto.BasePageVo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;


public class JobAuditVo extends BasePageVo {
    private static final Logger logger = LoggerFactory.getLogger(JobAuditVo.class);

    public final static String PROCESSING = "processing";
    
    public final static String SUCCESS = "success";
    
    public final static String ERROR = "error";
    private Long id;

    private Long jobId;

    private Date startTime;

    private Date endTime;
    @JSONField(serialize = false)
    private String logPath;
    @JSONField(serialize = false)
    private String logContent;

    private int isLogEmpty = 0;

    private String state = PROCESSING;
    
    private Integer serverId;

    public JobAuditVo() {
        this.setPageSize(20);
    }

    public JobAuditVo(Long _jobId) {
        this.jobId = _jobId;
    }

    public JobAuditVo(Long _jobId, Integer _serverId) {
        this.jobId = _jobId;
        this.serverId = _serverId;
    }
    
    public JobAuditVo(Long _id, String _logPath, String _errPath) {
        this.id = _id;
        this.logPath = _logPath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    private static String readContent(String filePath) {
        if (filePath == null || "".equals(filePath)) {
            return null;
        } else {
            String result = "";
            FileInputStream fr = null;
            File desFile = new File(filePath);
            try {
                if (desFile.isFile() && desFile.exists()) {
                    fr = new FileInputStream(desFile);
                    result = IOUtils.toString(fr, "utf-8");
                } else {
                    result = "出错，找不到对应的内容文件！";
                }
            } catch (Exception ex) {
                result = ex.getMessage();
                logger.error(ex.getMessage(), ex);
            } finally {
                if (fr != null) {
                    try {
                        fr.close();
                    } catch (IOException e) {
                        logger.error("FileInputStream close error : " + e.getMessage(), e);
                    }
                }
            }
            return result;
        }
    }

    public String getLogContent() {
        if(logContent == null){
            if(logPath!=null){
                logContent = readContent(logPath);
            }
        }
        return logContent;
    }

    public void setLogContent(String logContent) {
        this.logContent = logContent;
    }

    public int getIsLogEmpty() {
        if(readContent(logPath) != null && !"".equals(readContent(logPath))){
            isLogEmpty = 1;
        }
        return isLogEmpty;
    }

    public void setIsLogEmpty(int isLogEmpty) {
        this.isLogEmpty = isLogEmpty;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

	public Integer getServerId() {
		return serverId;
	}

	public void setServerId(Integer serverId) {
		this.serverId = serverId;
	}
}
