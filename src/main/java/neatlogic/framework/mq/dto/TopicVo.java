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
import neatlogic.framework.mq.core.IMqHandler;
import neatlogic.framework.mq.core.MqHandlerFactory;
import neatlogic.framework.mq.core.TopicFactory;
import neatlogic.framework.restful.annotation.EntityField;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class TopicVo {
    @EntityField(name = "唯一标识", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "名称", type = ApiParamType.STRING)
    private String label;
    @EntityField(name = "说明", type = ApiParamType.STRING)
    private String description;
    @EntityField(name = "是否激活", type = ApiParamType.INTEGER)
    private Integer isActive;
    @EntityField(name = "配置", type = ApiParamType.JSONOBJECT)
    private JSONObject config;
    @JSONField(serialize = false)
    private String configStr;
    @EntityField(name = "MQ类型", type = ApiParamType.STRING)
    private String handler;
    @EntityField(name = "MQ类型名称", type = ApiParamType.STRING)
    private String handlerName;
    @EntityField(name = "是否启用", type = ApiParamType.BOOLEAN)
    private Boolean isEnable;
    @EntityField(name = "是否系统内置主题", type = ApiParamType.BOOLEAN)
    private Boolean isEmbed;
    @EntityField(name = "是否需要配置", type = ApiParamType.BOOLEAN)
    private Boolean hasConfig;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TopicVo)) return false;
        TopicVo topicVo = (TopicVo) o;
        return Objects.equals(name, topicVo.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getIsActive() {
        if (isActive != null && isActive.equals(1)) {
            IMqHandler mqHandler = MqHandlerFactory.getMqHandler(handler);
            if (mqHandler != null && !mqHandler.isEnable()) {
                return 0;
            }
        }
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public JSONObject getConfig() {
        if (StringUtils.isNotBlank(configStr) && config == null) {
            try {
                config = JSON.parseObject(configStr);
            } catch (Exception ignored) {

            }
        }
        return config;
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

    public String getHandlerName() {
        if (StringUtils.isNotBlank(handler) && StringUtils.isBlank(handlerName)) {
            IMqHandler mqHandler = MqHandlerFactory.getMqHandler(handler);
            if (mqHandler != null) {
                handlerName = mqHandler.getLabel();
            }
        }
        return handlerName;
    }

    public void setConfig(JSONObject config) {
        this.config = config;
    }

    public String getConfigStr() {
        if (config != null) {
            configStr = config.toJSONString();
        }
        return configStr;
    }

    public void setConfigStr(String configStr) {
        this.configStr = configStr;
    }

    public Boolean getIsEmbed() {
        if (isEmbed == null) {
            isEmbed = TopicFactory.getTopicByName(this.name) != null;
        }
        return isEmbed;
    }

    public Boolean getHasConfig() {
        if (hasConfig == null) {
            hasConfig = TopicFactory.getTopic(this.name) == null || TopicFactory.getTopic(this.name).hasConfig();
        }
        return hasConfig;
    }

}
