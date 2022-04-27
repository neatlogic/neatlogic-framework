/*
 * Copyright (c)  2022 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dto.globallock;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;
import codedriver.framework.util.Md5Util;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class GlobalLockVo {
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


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        if(StringUtils.isNotBlank(key)) {
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
        if(MapUtils.isEmpty(handlerParam) && StringUtils.isNotBlank(handlerParamStr)){
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
}
