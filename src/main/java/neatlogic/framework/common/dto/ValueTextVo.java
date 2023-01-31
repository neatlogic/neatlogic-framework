/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.common.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;

import java.io.Serializable;

public class ValueTextVo implements Serializable {

    @EntityField(name = "value", type = ApiParamType.STRING)
    private Object value;
    @EntityField(name = "text", type = ApiParamType.STRING)
    private String text;

    public ValueTextVo() {
    }

    public ValueTextVo(Object value, String text) {
        this.value = value;
        this.text = text;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
