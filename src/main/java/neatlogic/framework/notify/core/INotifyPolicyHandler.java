/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.notify.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.dto.ConditionParamVo;
import neatlogic.framework.notify.constvalue.CommonNotifyParam;
import neatlogic.framework.notify.dto.NotifyTriggerVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public interface INotifyPolicyHandler {

    String getName();

    List<NotifyTriggerVo> getNotifyTriggerList();

    List<ValueTextVo> getParamTypeList();

    List<ConditionParamVo> getSystemParamList();

    List<ConditionParamVo> getSystemConditionOptionList();

    JSONObject getAuthorityConfig();

    default String getClassName() {
        return ClassUtils.getUserClass(this.getClass()).getName();
    }

    /**
     * 绑定权限，每种handler对应不同的权限
     */
    String getAuthName();

//    INotifyPolicyHandlerGroup getGroup();

    /**
     * 是否公开，默认公开
     */
    default boolean isPublic() {
        return true;
    }

    /**
     * 是否允许添加多个策略
     */
    default int isAllowMultiPolicy() {
        return 1;
    }

    default String getModuleGroup() {
        return null;
    }

    /**
     * 原来一个属性一个类型，改成从这里集中转换
     *
     * @return 转换后的参数，用于freeMarker替换变量
     */
    default JSONObject convertData(Object object, INotifyTriggerType notifyTriggerType) {
        JSONObject returnData = new JSONObject();
        String homeUrl = Config.HOME_URL();
        if (StringUtils.isNotBlank(homeUrl)) {
            if (!homeUrl.endsWith("/")) {
                homeUrl += "/";
            }
            returnData.put(CommonNotifyParam.HOMEURL.getValue(), homeUrl + TenantContext.get().getTenantUuid() + "/");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        returnData.put(CommonNotifyParam.CURRENT_TIME.getValue(), sdf.format(new Date()));
        UserContext userContext = UserContext.get();
        if (userContext != null) {
            returnData.put(CommonNotifyParam.OPERATOR.getValue(), userContext.getUserName());
        }
        return returnData;
    }

    default boolean needConvertData()
            throws NoSuchMethodException {
        Method interfaceMethod = INotifyPolicyHandler.class.getMethod("convertData", Object.class, INotifyTriggerType.class);
        Method classMethod = this.getClass().getMethod("convertData", Object.class, INotifyTriggerType.class);
        return !classMethod.equals(interfaceMethod);
    }
}
