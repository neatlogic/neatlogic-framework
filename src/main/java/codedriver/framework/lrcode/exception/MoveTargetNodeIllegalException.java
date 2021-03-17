package codedriver.framework.lrcode.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

/**
 * @Title: TreeNodeIllegalParameterException
 * @Package codedriver.framework.tree.service
 * @Description: 移动到的目标节点不合法异常
 * @Author: linbq
 * @Date: 2021/3/17 10:03
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class MoveTargetNodeIllegalException extends ApiRuntimeException {

    private static final long serialVersionUID = 4478080139019340483L;

    public MoveTargetNodeIllegalException(){
        super("移动到的目标节点不可以是当前节点及其后代节点");
    }
}
