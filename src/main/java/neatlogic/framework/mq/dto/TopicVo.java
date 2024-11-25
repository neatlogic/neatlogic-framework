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
