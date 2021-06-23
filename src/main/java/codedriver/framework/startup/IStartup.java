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
     * 执行主体方法
     */
    void execute();

    /**
     * 排序
     *
     * @return 顺序
     */
    int sort();
}
