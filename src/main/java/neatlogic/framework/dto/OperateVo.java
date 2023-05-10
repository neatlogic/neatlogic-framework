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

package neatlogic.framework.dto;

/**
 * @author: laiwt
 * @since: 2021/5/26 16:34
 **/
public class OperateVo {
    private String value;
    private String text;
    private Integer disabled = 0; // 是否可用
    private String disabledReason; // 不可用原因

    public OperateVo() {
    }

    public OperateVo(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public OperateVo(String value, String text, Integer disabled, String disabledReason) {
        this.value = value;
        this.text = text;
        this.disabled = disabled;
        this.disabledReason = disabledReason;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getDisabled() {
        return disabled;
    }

    public void setDisabled(Integer disabled) {
        this.disabled = disabled;
    }

    public String getDisabledReason() {
        return disabledReason;
    }

    public void setDisabledReason(String disabledReason) {
        this.disabledReason = disabledReason;
    }
}
