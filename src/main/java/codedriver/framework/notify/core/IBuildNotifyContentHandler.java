package codedriver.framework.notify.core;

import codedriver.framework.notify.dto.NotifyVo;
import org.springframework.util.ClassUtils;

import java.util.List;
import java.util.Map;

/**
 * @Title: IBuildNotifyContentHandler
 * @Package: codedriver.framework.notify.core
 * @Description:
 * @Author: laiwt
 * @Date: 2021/1/25 16:39
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public interface IBuildNotifyContentHandler {

    public List<NotifyVo> getNotifyVoList(Map<String,Object> map);

    public String getNotifyHandlerClassName();

    public String getNotifyContentHandlerClassName();

    public default String getClassName() {
        return ClassUtils.getUserClass(this.getClass()).getName();
    }
}
