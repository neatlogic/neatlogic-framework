package codedriver.framework.notify.core;

import codedriver.framework.notify.dto.NotifyVo;

import java.util.List;
import java.util.Map;

/**
 * @Title: BuildNotifyContentHandlerBase
 * @Package: codedriver.framework.notify.core
 * @Description:
 * @Author: laiwt
 * @Date: 2021/1/25 16:52
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public abstract class BuildNotifyContentHandlerBase implements IBuildNotifyContentHandler {

    @Override
    public List<NotifyVo> getNotifyVoList(Map<String, Object> map) {
        return myGetNotifyVoList(map);
    }

    @Override
    public String getNotifyHandlerClassName() {
        return myGetNotifyHandlerClassName();
    }

    @Override
    public String getNotifyContentHandlerClassName() {
        return myGetNotifyContentHandlerClassName();
    }

    protected abstract List<NotifyVo> myGetNotifyVoList(Map<String, Object> map);

    protected abstract String myGetNotifyHandlerClassName();

    protected abstract String myGetNotifyContentHandlerClassName();

}
