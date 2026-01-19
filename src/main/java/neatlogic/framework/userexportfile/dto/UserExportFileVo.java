/*
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package neatlogic.framework.userexportfile.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.userexportfile.core.IUserExportFileType;
import neatlogic.framework.userexportfile.core.UserExportFileTypeFactory;
import neatlogic.framework.util.$;
import neatlogic.framework.util.SnowflakeUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;

public class UserExportFileVo implements Serializable {
    private final static String[] units = new String[]{"字节", "KB", "MB", "GB"};
    private final static DecimalFormat decimalFormat = new DecimalFormat("0.##");

    private Long id;
    private String name;
    private Long size;
    private String sizeText;
    private String userUuid;
    private String type;
    private String typeText;
    private String contentType;
    private String path;
    private String prefix;
    private String suffix;
    private Date startTime;
    private Date endTime;
    private Integer isEnd;
    private Integer isRead;
    private String status;
    private String error;
    private JSONObject config;
    private String configStr;

    public UserExportFileVo() {

    }

    public UserExportFileVo(IUserExportFileType userExportFileType, String prefix, String suffix, String contentType) {
        this.type = userExportFileType.getValue();
        this.prefix = prefix;
        this.suffix = suffix;
        this.name = prefix + suffix;
        this.contentType = contentType;
        this.status = Status.DOING.value;
        this.config = new JSONObject();
        RequestContext requestContext = RequestContext.get();
        if (requestContext != null) {
            config.put("url", requestContext.getUrl());
            if (StringUtils.isNotBlank(requestContext.getParam())) {
                config.put("param", JSON.parseObject(requestContext.getParam()));
            }
        }
        this.userUuid = UserContext.get().getUserUuid();
        this.id = SnowflakeUtil.uniqueLong();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getSizeText() {
        if (sizeText == null && size != null) {
            double d = size;
            int unitindex = 0;
            while (d > 1024 && unitindex <= 3) {
                d = d / 1024;
                unitindex += 1;
            }
            sizeText = decimalFormat.format(d) + units[unitindex];
        }
        return sizeText;
    }

    public void setSizeText(String sizeText) {
        this.sizeText = sizeText;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeText() {
        if (typeText == null && type != null) {
            typeText = UserExportFileTypeFactory.getText(type);
        }
        return typeText;
    }

    public void setTypeText(String typeText) {
        this.typeText = typeText;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
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

    public Integer getIsEnd() {
        return isEnd;
    }

    public void setIsEnd(Integer isEnd) {
        this.isEnd = isEnd;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusText() {
        if (StringUtils.isNotBlank(status)) {
            return Status.getText(status);
        }
        return null;
    }
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public JSONObject getConfig() {
        if (config == null && configStr != null) {
            config = JSON.parseObject(configStr);
        }
        return config;
    }

    public void setConfig(JSONObject config) {
        this.config = config;
    }

    public String getConfigStr() {
        if (configStr == null && config != null) {
            configStr = config.toJSONString();
        }
        return configStr;
    }

    public void setConfigStr(String configStr) {
        this.configStr = configStr;
    }

    public enum Status {
        DOING("doing", "进行中"),
        DONE("done", "已完成"),
        FAILED("failed", "失败");

        private final String value;
        private final String text;

        Status(String _value, String _text) {
            this.value = _value;
            this.text = _text;
        }

        public String getValue() {
            return value;
        }

        public String getText() {
            return $.t(text);
        }


        public static String getText(String name) {
            for (Status s : Status.values()) {
                if (s.getValue().equals(name)) {
                    return s.getText();
                }
            }
            return "";
        }
    }
}
