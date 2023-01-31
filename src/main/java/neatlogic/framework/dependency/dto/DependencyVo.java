/*
 * Copyright(c) 2022 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
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
