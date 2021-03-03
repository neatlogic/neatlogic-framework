package codedriver.framework.restful.core;

import codedriver.framework.asynchronization.threadpool.ScheduledThreadPool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Title: SubmitKeyManager
 * @Package: codedriver.framework.restful.core
 * @Description: 管理重复提交key
 * @author: chenqiwei
 * @date: 2021/3/110:36 上午
 * Copyright(c) 2021 TechSure Co.,Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class SubmitKeyManager {
    private static final Map<String, Integer> SUBMIT_MAP = new ConcurrentHashMap<>();

    public static void add(String key, int timeout) {
        SUBMIT_MAP.put(key, timeout);
        ScheduledThreadPool.execute(new RemoveSubmitKeyThread(key) {
            @Override
            protected void execute() {
                SUBMIT_MAP.remove(this.getKey());
            }
        }, timeout);
    }

    public static boolean contain(String key) {
        return SUBMIT_MAP.containsKey(key);
    }

    public static void remove(String key) {
        SUBMIT_MAP.remove(key);
    }
}
