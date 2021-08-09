/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.asynchronization.threadlocal;

import codedriver.framework.common.constvalue.InputFrom;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class InputFromContext implements Serializable {
    private static final long serialVersionUID = -5732345436786224L;
    @JSONField(serialize = false)
    private final transient static ThreadLocal<InputFromContext> instance = new ThreadLocal<>();
    private InputFrom inputFrom;

    public static void init(InputFrom inputFrom) {
        InputFromContext context = new InputFromContext();
        context.setInputFrom(inputFrom);
        instance.set(context);
    }

    public static void init(InputFromContext _inputFromContext) {
        InputFromContext context = new InputFromContext();
        if (_inputFromContext != null && StringUtils.isNotBlank(_inputFromContext.getInputFrom())) {
            if (InputFrom.get(_inputFromContext.getInputFrom()) != null) {
                context.setInputFrom(InputFrom.get(_inputFromContext.getInputFrom()));
            }
        }
        if (StringUtils.isBlank(context.getInputFrom())) {
            context.setInputFrom(InputFrom.UNKNOWN);
        }
        instance.set(context);
    }

    public static InputFromContext get() {
        return instance.get();
    }

    public String getInputFrom() {
        if (inputFrom != null) {
            return inputFrom.getValue();
        }
        return null;
    }

    public void setInputFrom(InputFrom inputFrom) {
        this.inputFrom = inputFrom;
    }

    public void release() {
        instance.remove();
    }
}
