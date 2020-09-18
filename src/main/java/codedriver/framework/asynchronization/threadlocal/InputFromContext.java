package codedriver.framework.asynchronization.threadlocal;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

public class InputFromContext implements Serializable {
    private static final long serialVersionUID = -5732345436786224L;
    @JSONField(serialize=false)
    private transient static ThreadLocal<InputFromContext> instance = new ThreadLocal<InputFromContext>();
    private String inputFrom;

    public static InputFromContext init(String inputFrom) {
        InputFromContext context = new InputFromContext();
        context.setInputFrom(inputFrom);
        instance.set(context);
        return context;
    }

    public String getInputFrom() {
        return inputFrom;
    }

    public void setInputFrom(String inputFrom) {
        this.inputFrom = inputFrom;
    }

    public void release() {
        instance.remove();
    }
}
