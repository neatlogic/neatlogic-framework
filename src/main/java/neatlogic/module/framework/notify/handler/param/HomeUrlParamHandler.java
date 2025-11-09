/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.module.framework.notify.handler.param;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.notify.constvalue.CommonNotifyParam;
import neatlogic.framework.notify.core.INotifyParamHandler;
import neatlogic.framework.notify.core.INotifyTriggerType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author linbq
 * @since 2021/10/16 15:52
 **/
@Component
public class HomeUrlParamHandler implements INotifyParamHandler {

    @Override
    public String getValue() {
        return CommonNotifyParam.HOMEURL.getValue();
    }

    @Override
    public Object getText(Object object, INotifyTriggerType notifyTriggerType) {
        String homeUrl = Config.HOME_URL();
        if(StringUtils.isNotBlank(homeUrl)) {
            if(!homeUrl.endsWith("/")) {
                homeUrl += "/";
            }
            return homeUrl + TenantContext.get().getTenantUuid() + "/";
        }
        return null;
    }
}
