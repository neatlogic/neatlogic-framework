/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.scheduler.dto;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.restful.annotation.EntityField;
import codedriver.framework.util.SnowflakeUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class JobAuditVo extends BasePageVo {

//	public final static String RUNNING = "running";
//
//	public final static String SUCCEED = "succeed";
//
//	public final static String FAILED = "failed";

    public enum Status {
        SUCCEED("succeed", "成功", "#25b865"), RUNNING("running", "进行中", "#2d84fb"), FAILED("failed", "失败", "#f71010");

        private final String value;
        private final String text;
        private final String color;

        Status(String _value, String _text, String _color) {
            value = _value;
            text = _text;
            color = _color;
        }

        public String getValue() {
            return value;
        }

        public String getText() {
            return text;
        }

        public String getColor() {
            return color;
        }

        public static JSONObject getStatus(String value) {
            for (Status status : Status.values()) {
                if (status.getValue().equals(value)) {
                    JSONObject obj = new JSONObject();
                    obj.put("value", value);
                    obj.put("text", status.getText());
                    obj.put("color", status.getColor());
                    return obj;
                }
            }
            return null;
        }


    }

    @EntityField(name = "记录id", type = ApiParamType.LONG)
    private Long id;
    @EntityField(name = "定时作业uuid", type = ApiParamType.STRING)
    private String jobUuid;
    @EntityField(name = "开始时间", type = ApiParamType.LONG)
    private Date startTime;
    @EntityField(name = "结束时间", type = ApiParamType.LONG)
    private Date endTime;
    @EntityField(name = "日志内容", type = ApiParamType.STRING)
    private String content;
    @JSONField(serialize = false)
    private JSONObject startTimeRange;//搜索条件，开始时间范围

    private String contentHash;
    @EntityField(name = "执行状态(succeed：成功；failed:失败；running：进行中)", type = ApiParamType.STRING)
    private String status = null;
    @EntityField(name = "服务器id", type = ApiParamType.INTEGER)
    private Integer serverId;
    @EntityField(name = "日志内容长度", type = ApiParamType.INTEGER)
    private int contentLength;

    public JobAuditVo() {
        this.setPageSize(20);
    }

    public JobAuditVo(String _jobUUid, Integer _serverId) {
        this.jobUuid = _jobUUid;
        this.serverId = _serverId;
    }

    public List<Long> getStartTimeRange() {
        long startTime = 0L;
        long endTime = 0L;
        if (MapUtils.isNotEmpty(this.startTimeRange)) {
            String unit = this.startTimeRange.getString("timeUnit");
            String timeRange = this.startTimeRange.getString("timeRange");
            long st = this.startTimeRange.getLongValue("startTime");
            long et = this.startTimeRange.getLongValue("endTime");
            if (StringUtils.isNotBlank(unit) && StringUtils.isNotBlank(timeRange)) {
                endTime = System.currentTimeMillis() / 1000;
                int tr = Integer.parseInt(timeRange);
                Calendar now = Calendar.getInstance();
                switch (unit) {
                    case "day":
                        now.add(Calendar.DAY_OF_YEAR, -tr);
                        break;
                    case "week":
                        now.add(Calendar.WEEK_OF_YEAR, -tr);
                        break;
                    case "month":
                        now.add(Calendar.MONTH, -tr);
                        break;
                    case "year":
                        now.add(Calendar.YEAR, -tr);
                        break;
                }
                startTime = now.getTimeInMillis() / 1000;
            } else if (st > 0 && et > 0) {
                startTime = st;
                endTime = et;
            }
        }
        List<Long> startTimeRange = new ArrayList<>();
        if (startTime > 0 && endTime > 0 && startTime <= endTime) {
            startTimeRange.add(startTime);
            startTimeRange.add(endTime);
        }
        return startTimeRange;
    }

    public void setStartTimeRange(JSONObject startTimeRange) {
        this.startTimeRange = startTimeRange;
    }

    public Long getId() {
        if (id == null) {
            id = SnowflakeUtil.uniqueLong();
        }
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobUuid() {
        return jobUuid;
    }

    public void setJobUuid(String jobUuid) {
        this.jobUuid = jobUuid;
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

    public Integer getServerId() {
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void appendContent(String content) {
        if (StringUtils.isNotBlank(content)) {
            if (this.content == null) {
                this.content = "";
            }
            this.content += content;
        }
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public String getContentHash() {
        if (StringUtils.isBlank(contentHash) && StringUtils.isNotBlank(content)) {
            contentHash = DigestUtils.md5DigestAsHex(content.getBytes());
        }
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }
}
