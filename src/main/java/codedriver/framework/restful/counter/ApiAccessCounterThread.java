/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.restful.counter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadlocal.TenantContext;

/**
 * @Time:2020年7月15日
 * @ClassName: ApiAccessCounterThread
 * @Description: 模拟接口访问任务类
 */
public class ApiAccessCounterThread extends CodeDriverThread {

    private static final Logger logger = LoggerFactory.getLogger(ApiAccessCounterThread.class);

    private final String tenantUuid;

    private final String token;

    public ApiAccessCounterThread(int i, String tenantUuid, String token) {
        super("API-ACCESS-COUNTER" + i);
        this.tenantUuid = tenantUuid;
        this.token = token;
    }

    @Override
    protected void execute() {
        try {
            if (StringUtils.isNotBlank(tenantUuid) && StringUtils.isNotBlank(token)) {
                TenantContext.init(tenantUuid);
                ApiAccessCountUpdateThread.putToken(token);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
