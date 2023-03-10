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

package neatlogic.framework.integration.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BaseEditorVo;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.integration.core.IIntegrationHandler;
import neatlogic.framework.integration.core.IntegrationHandlerFactory;
import neatlogic.framework.restful.annotation.EntityField;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class IntegrationVo extends BaseEditorVo {
    private static final long serialVersionUID = 3921930881201126293L;
    @EntityField(name = "uuid", type = ApiParamType.STRING)
    private String uuid;
    @EntityField(name = "名称", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "目标地址", type = ApiParamType.STRING)
    private String url;
    @EntityField(name = "组件", type = ApiParamType.STRING)
    private String handler;
    @EntityField(name = "组件名称", type = ApiParamType.STRING)
    private String handlerName;
    @EntityField(name = "请求方法", type = ApiParamType.STRING)
    private String method;
    @EntityField(name = "是否激活", type = ApiParamType.INTEGER)
    private Integer isActive;
    @EntityField(name = "输入参数模板", type = ApiParamType.JSONARRAY)
    private List<PatternVo> inputPatternList = new ArrayList<>();
    @EntityField(name = "输出参数模板", type = ApiParamType.JSONARRAY)
    private List<PatternVo> outputPatternList = new ArrayList<>();
    @EntityField(name = "配置", type = ApiParamType.JSONOBJECT)
    private JSONObject config;
    @EntityField(name = "被引用次数", type = ApiParamType.INTEGER)
    private int referenceCount;
    // 请求参数
    @JSONField(serialize = false)
    private JSONObject paramObj = new JSONObject();
    @EntityField(name = "是否有帮助", type = ApiParamType.INTEGER)
    private Integer hasHelp;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        if (StringUtils.isBlank(uuid)) {
            uuid = UUID.randomUUID().toString().replace("-", "");
        }
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getHandlerName() {
        if (StringUtils.isBlank(handlerName) && StringUtils.isNotBlank(handler)) {
            IIntegrationHandler integrationHandler = IntegrationHandlerFactory.getHandler(handler);
            if (integrationHandler != null) {
                handlerName = integrationHandler.getName();
            }
        }
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    @JSONField(serialize = false)
    public String getConfigStr() {
        if (config != null) {
            return config.toJSONString();
        }
        return null;
    }

    public void setConfig(String config) {
        try {
            this.config = JSONObject.parseObject(config);
        } catch (Exception ex) {

        }
    }

    public JSONObject getConfig() {
        return config;
    }

    public int getReferenceCount() {
        return referenceCount;
    }

    public void setReferenceCount(int referenceCount) {
        this.referenceCount = referenceCount;
    }

    public JSONObject getParamObj() {
        return paramObj;
    }

    public void setParamObj(JSONObject paramObj) {
        this.paramObj = paramObj;
    }

    public List<PatternVo> getInputPatternList() {
        return inputPatternList;
    }

    public void setInputPatternList(List<PatternVo> inputPatternList) {
        this.inputPatternList = inputPatternList;
    }

    public List<PatternVo> getOutputPatternList() {
        return outputPatternList;
    }

    public void setOutputPatternList(List<PatternVo> outputPatternList) {
        this.outputPatternList = outputPatternList;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Integer getHasHelp() {
        if (hasHelp == null && StringUtils.isNotBlank(handler)) {
            IIntegrationHandler integrationHandler = IntegrationHandlerFactory.getHandler(handler);
            if (integrationHandler != null) {
                hasHelp = integrationHandler.hasPattern();
            }
            if ((hasHelp == null || hasHelp.equals(0)) && this.config != null && this.config.containsKey("param")) {
                if (this.config.getJSONObject("param").containsKey("paramList") && this.config.getJSONObject("param").getJSONArray("paramList").size() > 0) {
                    hasHelp = 1;
                }
            }
        }
        return hasHelp;
    }

    public void setHasHelp(Integer hasHelp) {
        this.hasHelp = hasHelp;
    }

}
