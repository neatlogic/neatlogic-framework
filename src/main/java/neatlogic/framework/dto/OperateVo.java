/*Copyright (C) 2023  深圳极向量科技有限公司 All Rights Reserved.

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
