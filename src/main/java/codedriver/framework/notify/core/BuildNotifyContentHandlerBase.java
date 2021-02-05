package codedriver.framework.notify.core;

import codedriver.framework.notify.dto.NotifyVo;
import codedriver.framework.notify.dto.job.NotifyJobVo;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

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
    public String getPreviewContent(JSONObject config) {
        return myGetPreviewContent(config);
    }

    @Override
    public List<NotifyVo> getNotifyVoList(NotifyJobVo job) {
        return myGetNotifyVoList(job);
    }

    @Override
    public String getNotifyHandlerClassName() {
        return myGetNotifyHandlerClassName();
    }

    @Override
    public String getNotifyContentHandlerClassName() {
        return myGetNotifyContentHandlerClassName();
    }

    protected abstract String myGetPreviewContent(JSONObject config);

    protected abstract List<NotifyVo> myGetNotifyVoList(NotifyJobVo job);

    protected abstract String myGetNotifyHandlerClassName();

    protected abstract String myGetNotifyContentHandlerClassName();

}
