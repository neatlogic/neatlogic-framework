/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.restful.core;

import codedriver.framework.asynchronization.thread.CodeDriverThread;

public abstract class RemoveSubmitKeyThread extends CodeDriverThread {
    private final String key;

    public RemoveSubmitKeyThread(String _key) {
        super("SUBMIT-KEY-REMOVER");
        key = _key;
    }

    protected String getKey() {
        return key;
    }
}
