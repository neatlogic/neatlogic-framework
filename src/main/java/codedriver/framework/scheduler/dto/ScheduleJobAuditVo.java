package codedriver.framework.scheduler.dto;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codedriver.framework.common.dto.BasePageVo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class ScheduleJobAuditVo extends BasePageVo {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleJobAuditVo.class);

    private Long id;

    private Long jobId;

    private String startTime;

    private String endTime;

    private String logPath;

    private String errPath;

    private String logContent;

    private String errContent;

    private int isLogEmpty = 0;

    private int isErrEmpty = 0;

    private String state = JobAuditState.PROCEED.getName();


    public enum JobAuditState{
        PROCEED("proceed","进行中"),FINISH("finish","完成"),FAULT("fault","异常");
        private String name;
        private String value;

        private JobAuditState(String _name,String _value){
            this.name = _name;
            this.value = _value;
        }

        public String getName(){
            return name;
        }

        public String getValue(){
            return value;
        }

        public static String getValue(String _name){
            for(JobAuditState s:JobAuditState.values()){
                if(s.getName().equals(_name)){
                    return s.getValue();
                }
            }
            return "";
        }
    }


    public ScheduleJobAuditVo() {
        this.setPageSize(20);
    }

    public ScheduleJobAuditVo(Long _jobId) {
        this.jobId = _jobId;
    }

    public ScheduleJobAuditVo(Long _id, String _logPath, String _errPath) {
        this.id = _id;
        this.logPath = _logPath;
        this.errPath = _errPath;
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

    public String getErrPath() {
        return errPath;
    }

    public void setErrPath(String errPath) {
        this.errPath = errPath;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
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

    public String getErrContent() {
        if(errContent==null){
            if(errPath!=null){
                errContent = readContent(errPath);
            }
        }

        return errContent;
    }

    public void setErrContent(String errContent) {
        this.errContent = errContent;
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

    public int getIsErrEmpty() {
        if(readContent(errPath) != null && !"".equals(readContent(errPath))){
            isErrEmpty = 1;
        }
        return isErrEmpty;
    }

    public void setIsErrEmpty(int isErrEmpty) {
        this.isErrEmpty = isErrEmpty;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
