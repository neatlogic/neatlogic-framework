package neatlogic.framework.importexport.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

import java.util.ArrayList;
import java.util.List;

public class DependencyNotFoundException extends ApiRuntimeException {

    private List<String> messageList;

    public DependencyNotFoundException(List<String> messageList) {
        this.messageList = messageList;
    }

    public DependencyNotFoundException(String message) {
        this.messageList = new ArrayList<>();
        this.messageList.add(message);
    }

    public List<String> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<String> messageList) {
        this.messageList = messageList;
    }
}
