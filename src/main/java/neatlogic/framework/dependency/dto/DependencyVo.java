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

package neatlogic.framework.dependency.dto;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author linbq
 * @since 2022/1/6 15:59
 **/
public class DependencyVo {
    private String from;
    private String type;
    private String to;
    private JSONObject config;
    private String configStr;

    public DependencyVo(){}

    public DependencyVo(String type, String to) {
        this.type = type;
        this.to = to;
    }

    public DependencyVo(String type, String from, String to) {
        this.type = type;
        this.from = from;
        this.to = to;
    }
    public DependencyVo(String from, String type, String to, JSONObject config) {
        this.from = from;
        this.type = type;
        this.to = to;
        this.config = config;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public JSONObject getConfig() {
        if (config == null && StringUtils.isNotBlank(configStr)) {
            config = JSONObject.parseObject(configStr);
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
}
