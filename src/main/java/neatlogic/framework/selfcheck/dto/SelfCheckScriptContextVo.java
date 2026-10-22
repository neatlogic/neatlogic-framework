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

package neatlogic.framework.selfcheck.dto;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

/** 仅包含可安全复制到 JavaScript 沙箱的数据。 */
public class SelfCheckScriptContextVo {
    private JSONObject request;
    private Object response;
    private JSONObject target;
    private JSONObject run;
    private JSONObject timing;
    @JSONField(serialize = false)
    private List<String> sensitiveValueList = new ArrayList<>();

    /** 构造传入脚本的纯 JSON 对象，不向脚本暴露 Java 实例。 */
    public JSONObject toScriptJson() {
        JSONObject jsonObject = new JSONObject(true);
        jsonObject.put("request", request);
        jsonObject.put("response", response);
        jsonObject.put("target", target);
        jsonObject.put("run", run);
        jsonObject.put("timing", timing);
        return jsonObject;
    }

    public JSONObject getRequest() {
        return request;
    }

    public void setRequest(JSONObject request) {
        this.request = request;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public JSONObject getTarget() {
        return target;
    }

    public void setTarget(JSONObject target) {
        this.target = target;
    }

    public JSONObject getRun() {
        return run;
    }

    public void setRun(JSONObject run) {
        this.run = run;
    }

    public JSONObject getTiming() {
        return timing;
    }

    public void setTiming(JSONObject timing) {
        this.timing = timing;
    }

    public List<String> getSensitiveValueList() {
        return sensitiveValueList;
    }

    public void setSensitiveValueList(List<String> sensitiveValueList) {
        this.sensitiveValueList = sensitiveValueList;
    }
}
