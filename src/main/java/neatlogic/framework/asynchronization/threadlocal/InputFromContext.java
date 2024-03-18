/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.asynchronization.threadlocal;

import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.constvalue.InputFrom;
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
