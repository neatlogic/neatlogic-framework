/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.scheduler.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;
import neatlogic.framework.util.SnowflakeUtil;
import neatlogic.framework.util.TimeUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
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
        SUCCEED("succeed", new I18n("common.success"), "#25b865"),
        RUNNING("running", new I18n("common.ongoing"), "#2d84fb"),
        FAILED("failed", new I18n("common.fail"), "#f71010");

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
            return I18nUtils.getMessage(text.toString());
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
}
