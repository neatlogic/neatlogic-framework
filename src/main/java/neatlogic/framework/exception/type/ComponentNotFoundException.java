package neatlogic.framework.exception.type;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ComponentNotFoundException extends ApiRuntimeException {

    /**
     * @Fields serialVersionUID : TODO
     */
    private static final long serialVersionUID = -6165807991291970685L;

    public ComponentNotFoundException(String msg) {
        super("接口组件:{0}不存在", msg);
    }

}
