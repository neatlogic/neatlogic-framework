/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.framework.dto.globallock;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.Md5Util;
import neatlogic.framework.util.SnowflakeUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

public class GlobalLockVo extends BasePageVo {
    private static final long serialVersionUID = 6246879548347033138L;
    @EntityField(name = "锁id", type = ApiParamType.LONG)
    private Long id;
    @EntityField(name = "key md5", type = ApiParamType.STRING)
    private String uuid;
    @EntityField(name = "key", type = ApiParamType.STRING)
    private String key;
    @EntityField(name = "使用方", type = ApiParamType.STRING)
    private String handler;
    @JSONField(serialize = false)
    private String handlerParamStr;
    @EntityField(name = "使用方入参", type = ApiParamType.JSONOBJECT)
    private JSONObject handlerParam;
    @EntityField(name = "是否上锁,1:上锁,0:未锁,队列中", type = ApiParamType.INTEGER)
    private Integer isLock = 0;
    @EntityField(name = "描述", type = ApiParamType.STRING)
    private String description;
    @EntityField(name = "进队列时间", type = ApiParamType.LONG)
    private Date fcd;
    @EntityField(name = "上锁时间", type = ApiParamType.LONG)
    private Date lcd;
    @EntityField(name = "wait 原因", type = ApiParamType.STRING)
    private String waitReason;
    @EntityField(name = "过滤关键词", type = ApiParamType.JSONOBJECT)
    private JSONObject keywordParam;

    @JSONField(serialize = false)
    private List<String> uuidList;//用于过滤

    public GlobalLockVo() {

    }

    public GlobalLockVo(String handler, String key, String handlerParamStr) {
        init(null, handler, key, handlerParamStr, null);
    }

    public GlobalLockVo(String handler, String key, String handlerParamStr, String description) {
        init(null, handler, key, handlerParamStr, description);
    }

    public GlobalLockVo(Long lockId, String handler, String key, String handlerParamStr, String description) {
        init(lockId, handler, key, handlerParamStr, description);
    }

    public GlobalLockVo(Long lockId, String handler, String key, String handlerParamStr) {
        init(lockId, handler, key, handlerParamStr, null);
    }

    private void init(Long lockId, String handler, String key, String handlerParamStr, String description) {
        this.id = lockId;
        this.handler = handler;
        this.key = key;
        this.handlerParamStr = handlerParamStr;
        this.description = description;
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

    public String getUuid() {
        if (StringUtils.isNotBlank(key)) {
            uuid = Md5Util.encryptMD5(key);
        }
        return uuid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getIsLock() {
        return isLock;
    }

    public void setIsLock(Integer isLock) {
        this.isLock = isLock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getFcd() {
        return fcd;
    }

    public void setFcd(Date fcd) {
        this.fcd = fcd;
    }

    public Date getLcd() {
        return lcd;
    }

    public void setLcd(Date lcd) {
        this.lcd = lcd;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getHandlerParamStr() {
        return handlerParamStr;
    }

    public void setHandlerParamStr(String handlerParamStr) {
        this.handlerParamStr = handlerParamStr;
    }

    public JSONObject getHandlerParam() {
        if (MapUtils.isEmpty(handlerParam) && StringUtils.isNotBlank(handlerParamStr)) {
            handlerParam = JSONObject.parseObject(handlerParamStr);
        }
        return handlerParam;
    }

    public String getWaitReason() {
        return waitReason;
    }

    public void setWaitReason(String waitReason) {
        this.waitReason = waitReason;
    }

    public JSONObject getKeywordParam() {
        return keywordParam;
    }

    public void setKeywordParam(JSONObject keywordParam) {
        this.keywordParam = keywordParam;
    }

    public List<String> getUuidList() {
        return uuidList;
    }

    public void setUuidList(List<String> uuidList) {
        this.uuidList = uuidList;
    }
}
