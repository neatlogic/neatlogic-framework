package neatlogic.framework.message.dto;

/**
 * @Title: TriggerMessageCountVo
 * @Package neatlogic.framework.message.dto
 * @Description: TODO
 * @Author: linbq
 * @Date: 2021/2/22 18:46
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
 **/
public class TriggerMessageCountVo {
    private String notifyPolicyHandler;
    private String trigger;
    private Integer count;
    private Integer isRead;

    public String getNotifyPolicyHandler() {
        return notifyPolicyHandler;
    }

    public void setNotifyPolicyHandler(String notifyPolicyHandler) {
        this.notifyPolicyHandler = notifyPolicyHandler;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }
}
