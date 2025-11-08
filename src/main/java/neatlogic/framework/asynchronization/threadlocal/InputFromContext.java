/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.asynchronization.threadlocal;

import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.constvalue.InputFrom;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class InputFromContext implements Serializable {
    private static final long serialVersionUID = -5732345436786224L;
    @JSONField(serialize = false)
    private static final ThreadLocal<InputFromContext> instance = new ThreadLocal<>();
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
