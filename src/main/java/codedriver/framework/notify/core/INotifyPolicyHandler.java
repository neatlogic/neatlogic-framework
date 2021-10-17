/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.notify.core;

import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.dto.ConditionParamVo;
import codedriver.framework.notify.dto.NotifyTriggerTemplateVo;
import codedriver.framework.notify.dto.NotifyTriggerVo;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.ClassUtils;

import java.util.List;

public interface INotifyPolicyHandler {

    String getName();

    List<NotifyTriggerVo> getNotifyTriggerList();

    /**
     * 获取通知触发点模版列表
     */
    List<NotifyTriggerTemplateVo> getNotifyTriggerTemplateList(NotifyHandlerType type);

    List<ValueTextVo> getParamTypeList();

    List<ConditionParamVo> getSystemParamList();

    List<String> getParamNameList();

    List<ConditionParamVo> getSystemConditionOptionList();

    JSONObject getAuthorityConfig();

    default String getClassName() {
        return ClassUtils.getUserClass(this.getClass()).getName();
    }

    /**
     * 绑定权限，每种handler对应不同的权限
     */
    String getAuthName();

    INotifyPolicyHandlerGroup getGroup();

    /**
     * 是否公开，默认公开
     *
     * @return
     */
    default boolean isPublic() {
        return true;
    }
}
