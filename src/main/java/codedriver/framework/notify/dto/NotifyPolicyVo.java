package codedriver.framework.notify.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.dto.BaseEditorVo;
import codedriver.framework.restful.annotation.EntityField;
import codedriver.framework.util.SnowflakeUtil;

public class NotifyPolicyVo extends BaseEditorVo {

    @EntityField(name = "策略id", type = ApiParamType.LONG)
    private Long id;
    @EntityField(name = "策略名", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "被引用次数", type = ApiParamType.INTEGER)
    private int referenceCount;
    @EntityField(name = "配置项信息", type = ApiParamType.JSONOBJECT)
    private NotifyPolicyConfigVo config;
    @EntityField(name = "通知策略处理器", type = ApiParamType.STRING)
    private String handler;

    public NotifyPolicyVo() {

    }

    public NotifyPolicyVo(String name, String handler) {
        this.name = name;
        this.handler = handler;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getReferenceCount() {
        return referenceCount;
    }

    public void setReferenceCount(int referenceCount) {
        this.referenceCount = referenceCount;
    }

    public NotifyPolicyConfigVo getConfig() {
        return config;
    }

    public void setConfig(String config) {
        try {
            this.config = JSON.parseObject(config, NotifyPolicyConfigVo.class);
        } catch (Exception ex) {

        }
    }

    @JSONField(serialize = false)
    public String getConfigStr() {
        if (config != null) {
            return JSON.toJSONString(config);
        }
        return null;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

}
