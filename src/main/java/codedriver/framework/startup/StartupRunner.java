/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartupRunner implements Runnable {
    Logger logger = LoggerFactory.getLogger(StartupRunner.class);
    private IStartup startup;

    public StartupRunner(IStartup _startup) {
        startup = _startup;
    }

    @Override
    public void run() {
        try {
            //等待所有模块加载完毕才往下执行代码
            startup.execute();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
