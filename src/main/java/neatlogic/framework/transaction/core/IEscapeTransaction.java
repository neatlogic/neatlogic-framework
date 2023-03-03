package neatlogic.framework.transaction.core;

/**
 * @Title: ICommit
 * @Package: neatlogic.framework.transaction.core
 * @Description: 去掉事务执行
 * @author: chenqiwei
 * @date: 2021/1/73:38 下午
 **/
public interface IEscapeTransaction {
    void execute();
}
