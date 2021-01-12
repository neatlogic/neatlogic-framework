package codedriver.framework.message.delay;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadpool.CommonThreadPool;
import codedriver.framework.restful.counter.ApiAccessCounterThread;
import org.apache.poi.ss.formula.functions.T;

import java.util.Arrays;
import java.util.List;

/**
 * @Title: Test
 * @Package codedriver.framework.message.delay
 * @Description: TODO
 * @Author: linbq
 * @Date: 2021/1/12 7:29
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class Test {
    /** 租户列表 **/
    private static List<String> tenantUuidList = Arrays.asList("tenant1", "tenant2","tenant3");

    public static void main(String[] args) throws InterruptedException {
        for(int i = 0; i < 10; i++) {
            Thread.sleep(10);
            String tenantUuid = tenantUuidList.get(i % tenantUuidList.size());
            TenantContext.init(tenantUuid);
            CommonThreadPool.execute(new MessageProducerThread());
            System.out.println(CommonThreadPool.getWorkQueueSize());
        }
        while(true) {
            System.out.println(CommonThreadPool.getWorkQueueSize());
            if(CommonThreadPool.getWorkQueueSize() == 0){
                break;
            }
            Thread.sleep(10);
        }
        System.out.println("end...");
    }
}
