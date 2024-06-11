package neatlogic.framework.batch;

/**
 * @Title: BatchJob
 * @Package neatlogic.framework.batch
 * @Description: 批量任务接口
 * @Author: chenqiwei
 * @Date: 2021/1/4 9:32 上午
 **/
public interface BatchJob<T> {
    void execute(int threadIndex, int dataIndex, T item);
}
