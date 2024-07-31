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

package neatlogic.framework.scheduler.dto;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;
import neatlogic.framework.util.SnowflakeUtil;
import neatlogic.framework.util.TimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

public class JobAuditVo extends BasePageVo {

//	public final static String RUNNING = "running";
//
//	public final static String SUCCEED = "succeed";
//
//	public final static String FAILED = "failed";

    public enum Status {
        SUCCEED("succeed", new I18n("成功"), "#25b865"),
        RUNNING("running", new I18n("进行中"), "#2d84fb"),
        FAILED("failed", new I18n("失败"), "#f71010");

        private final String value;
        private final I18n text;
        private final String color;

        Status(String _value, I18n _text, String _color) {
            value = _value;
            text = _text;
            color = _color;
        }

        public String getValue() {
            return value;
        }

        public String getText() {
            return $.t(text.toString());
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
    @EntityField(name = "日志内容哈希", type = ApiParamType.INTEGER)
    private int contentLength;
    @EntityField(name = "cron表达式", type = ApiParamType.STRING)
    private String cron;
    @EntityField(name = "下次激活时间", type = ApiParamType.LONG)
    private Date nextFireTime;
    @EntityField(name = "作业组名", type = ApiParamType.STRING)
    private String jobGroupName;

    public JobAuditVo() {
        this.setPageSize(20);
    }

    public JobAuditVo(String _jobUUid, Integer _serverId) {
        this.jobUuid = _jobUUid;
        this.serverId = _serverId;
    }

    public List<Long> getStartTimeRange() {
        return TimeUtil.getTimeRangeList(this.startTimeRange);
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

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public Date getNextFireTime() {
        return nextFireTime;
    }

    public void setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    public String getJobGroupName() {
        return jobGroupName;
    }

    public void setJobGroupName(String jobGroupName) {
        this.jobGroupName = jobGroupName;
    }
}
