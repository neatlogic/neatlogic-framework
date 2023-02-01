/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.notify.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author linbq
 * @since 2021/5/20 18:08
 **/
public class InvokeNotifyPolicyConfigVo {
    private Long policyId;

    private String policyName;

    private String policyPath;
    private List<NotifyPolicyParamMappingVo> paramMappingList = new ArrayList<>();
    private String handler;
    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getPolicyPath() {
        return policyPath;
    }

    public void setPolicyPath(String policyPath) {
        this.policyPath = policyPath;
    }

    public List<NotifyPolicyParamMappingVo> getParamMappingList() {
        return paramMappingList;
    }

    public void setParamMappingList(List<NotifyPolicyParamMappingVo> paramMappingList) {
        this.paramMappingList = paramMappingList;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }
}
