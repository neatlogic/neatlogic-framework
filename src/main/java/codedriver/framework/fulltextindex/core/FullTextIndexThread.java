package codedriver.framework.fulltextindex.core;

import codedriver.framework.asynchronization.thread.CodeDriverThread;

/**
 * @Title: FullTextIndexThread
 * @Package: codedriver.framework.fulltextindex.core
 * @Description: TODO
 * @author: chenqiwei
 * @date: 2021/2/2612:11 下午
 * Copyright(c) 2021 TechSure Co.,Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public abstract class FullTextIndexThread extends CodeDriverThread {
    private final Long targetId;
    private final String targetType;

    public Long getTargetId() {
        return targetId;
    }

    public String getTargetType() {
        return targetType;
    }

    public FullTextIndexThread(Long _targetId, String _targetType) {
        this.targetId = _targetId;
        this.targetType = _targetType;
    }

}
