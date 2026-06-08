/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.mq.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.mq.core.*;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.SnowflakeUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class SubscribeVo extends BasePageVo {
    @EntityField(name = "id", type = ApiParamType.LONG)
    private Long id;
    @EntityField(name = "唯一标识", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "处理类名", type = ApiParamType.STRING)
    private String className;
    @EntityField(name = "处理器唯一标识", type = ApiParamType.STRING)
    private String subscribeHandlerName;
    @EntityField(name = "主题唯一标识", type = ApiParamType.STRING)
    private String topicName;
    @EntityField(name = "主题名称", type = ApiParamType.STRING)
    private String topicLabel;
    @EntityField(name = "是否持久订阅", type = ApiParamType.INTEGER)
    private Integer isDurable;
    @EntityField(name = "描述", type = ApiParamType.STRING)
    private String description;
    @EntityField(name = "状态", type = ApiParamType.STRING)
    private String status;
    @EntityField(name = "状态名称", type = ApiParamType.STRING)
    private String statusText;
    @EntityField(name = "是否激活", type = ApiParamType.INTEGER)
    private Integer isActive;
    @EntityField(name = "异常", type = ApiParamType.STRING)
    private String error;
    @EntityField(name = "配置", type = ApiParamType.JSONOBJECT)
    private JSONObject config;
    @JSONField(serialize = false)
    private String tenantUuid;
    @JSONField(serialize = false)
    private String configStr;
    @JSONField(serialize = false)
    private Integer serverId;
    @EntityField(name = "MQ类型", type = ApiParamType.STRING)
    private String handler;
    @EntityField(name = "消息队列名称", type = ApiParamType.STRING)
    private String handlerName;
    @EntityField(name = "是否启用", type = ApiParamType.BOOLEAN)
    private Boolean isEnable;
    @EntityField(name = "是否系统内置订阅", type = ApiParamType.BOOLEAN)
    private Boolean isEmbed;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SubscribeVo)) return false;
        SubscribeVo that = (SubscribeVo) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getSubscribeHandlerName() {
        if (StringUtils.isNotBlank(className)) {
            ISubscribeHandler handler = SubscribeHandlerFactory.getHandler(className);
            if (handler != null) {
                return handler.getName();
            }
        }
        return subscribeHandlerName;
    }

    public void setSubscribeHandlerName(String subscribeHandlerName) {
        this.subscribeHandlerName = subscribeHandlerName;
    }

    public Long getId() {
        if (id == null) {
            id = SnowflakeUtil.uniqueLong();
        }
        return id;
    }

    public String getTenantUuid() {
        return tenantUuid;
    }

    public Boolean getIsEnable() {
        if (StringUtils.isNotBlank(handler) && isEnable == null) {
            IMqHandler mqHandler = MqHandlerFactory.getMqHandler(handler);
            if (mqHandler != null) {
                isEnable = mqHandler.isEnable();
            }
        }
        return isEnable;
    }

    public Boolean getIsEmbed() {
        if (isEmbed == null) {
            isEmbed = SubscribeHandlerFactory.hasSystemSubscribe(name);
        }
        return isEmbed;
    }

    public void setIsEmbed(Boolean isEmbed) {
        this.isEmbed = isEmbed;
    }

    public void setTenantUuid(String tenantUuid) {
        this.tenantUuid = tenantUuid;
    }

    public String getTopicLabel() {
        if (StringUtils.isBlank(topicLabel) && StringUtils.isNotBlank(topicName)) {
            TopicVo topicVo = TopicFactory.getTopicByName(topicName);
            if (topicVo != null) {
                topicLabel = topicVo.getLabel();
            }
        }
        return topicLabel;
    }

    public String getHandlerName() {
        if (StringUtils.isNotBlank(handler) && StringUtils.isBlank(handlerName)) {
            IMqHandler mqHandler = MqHandlerFactory.getMqHandler(handler);
            if (mqHandler != null) {
                handlerName = mqHandler.getLabel();
            }
        }
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public void setTopicLabel(String topicLabel) {
        this.topicLabel = topicLabel;
    }

    public Integer getServerId() {
        return serverId;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIsDurable() {
        return isDurable;
    }

    public void setIsDurable(Integer isDurable) {
        this.isDurable = isDurable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public Integer getIsActive() {
        if (isActive != null && isActive.equals(1) && StringUtils.isNotBlank(handler)) {
            IMqHandler mqHandler = MqHandlerFactory.getMqHandler(handler);
            if (mqHandler == null || !mqHandler.isEnable()) {
                return 0;
            }
        }
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public JSONObject getConfig() {
        if (config == null && StringUtils.isNotBlank(configStr)) {
            try {
                config = JSON.parseObject(configStr);
            } catch (Exception ignored) {

            }
        }
        return config;
    }

    public void setConfig(JSONObject config) {
        this.config = config;
    }

    public String getConfigStr() {
        if (config != null) {
            configStr = config.toString();
        }
        return configStr;
    }

    public void setConfigStr(String configStr) {
        this.configStr = configStr;
    }
}
