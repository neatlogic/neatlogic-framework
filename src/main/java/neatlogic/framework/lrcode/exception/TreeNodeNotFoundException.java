package neatlogic.framework.lrcode.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

/**
 * @Title: TreeNodeNotFoundException
 * @Package neatlogic.framework.tree.exception
 * @Description: 树节点不存在异常
 * @Author: linbq
 * @Date: 2021/3/17 7:12

 **/
public class TreeNodeNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 4478080139019340482L;

    public TreeNodeNotFoundException(String tableName, Object idValue) {
        super("“{0}”表中树节点：“{1}”不存在", tableName, idValue);
    }
}
