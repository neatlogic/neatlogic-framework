/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.asynchronization.threadlocal;

import neatlogic.framework.common.constvalue.InputFrom;
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
