package neatlogic.framework.lrcode.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

/**
 * @Title: TreeNodeIllegalParameterException
 * @Package neatlogic.framework.tree.service
 * @Description: 移动到的目标节点不合法异常
 * @Author: linbq
 * @Date: 2021/3/17 10:03

 **/
public class MoveTargetNodeIllegalException extends ApiRuntimeException {

    private static final long serialVersionUID = 4478080139019340483L;

    public MoveTargetNodeIllegalException(){
        super("移动到的目标节点不可以是当前节点及其后代节点");
    }
}
