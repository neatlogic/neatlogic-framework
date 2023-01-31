package neatlogic.framework.lrcode.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

/**
 * @Title: TreeNodeNotFoundException
 * @Package neatlogic.framework.tree.exception
 * @Description: 树节点不存在异常
 * @Author: linbq
 * @Date: 2021/3/17 7:12
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class TreeNodeNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 4478080139019340482L;

    public TreeNodeNotFoundException(String tableName, Object idValue){
        super("'" + tableName + "'表中树节点：'" + idValue + "'不存在");
    }
}
