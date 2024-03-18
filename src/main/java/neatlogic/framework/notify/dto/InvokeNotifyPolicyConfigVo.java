/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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
    private String policyPath = "";
    private List<ParamMappingVo> paramMappingList = new ArrayList<>();
    private String handler;
    private int isCustom = 0;

    private List<String> excludeTriggerList = new ArrayList<>();

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

    public List<ParamMappingVo> getParamMappingList() {
        return paramMappingList;
    }

    public void setParamMappingList(List<ParamMappingVo> paramMappingList) {
        this.paramMappingList = paramMappingList;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public int getIsCustom() {
        return isCustom;
    }

    public void setIsCustom(int isCustom) {
        this.isCustom = isCustom;
    }

    public List<String> getExcludeTriggerList() {
        return excludeTriggerList;
    }

    public void setExcludeTriggerList(List<String> excludeTriggerList) {
        this.excludeTriggerList = excludeTriggerList;
    }
}
