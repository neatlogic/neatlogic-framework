/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.startup;

public interface IStartup {
    /**
     * 作业名称
     *
     * @return 字符串
     */
    String getName();

    /**
     * 每个租户分别执行
     */
    void executeForCurrentTenant();

    /*
    只执行一次，晚于executeForTenant执行
     */
    void executeForAllTenant();

    /**
     * 排序
     *
     * @return 顺序
     */
    int sort();

    /**
     * 设置所在模块组名
     * @param groupName
     */
    void setGroupName(String groupName);

    /**
     * 获取所在模块组名
     * @return
     */
    String getGroupName();
}
