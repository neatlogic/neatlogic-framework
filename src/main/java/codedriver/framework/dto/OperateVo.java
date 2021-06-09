/*
 * Copyright(c) 2021. TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dto;

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
