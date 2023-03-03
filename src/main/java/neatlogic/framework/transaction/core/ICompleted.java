package neatlogic.framework.transaction.core;

/**
 * @Title: ICommit
 * @Package: neatlogic.framework.transaction.core
 * @Description: TODO
 * @author: chenqiwei
 * @date: 2021/1/73:38 下午
 **/
public interface ICompleted<T> {
    void execute(T t);
}
