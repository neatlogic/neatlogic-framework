/*
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.module.framework.notify.handler.param;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.notify.constvalue.CommonNotifyParam;
import neatlogic.framework.notify.core.INotifyParamHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author linbq
 * @since 2021/10/16 15:52
 **/
@Component
public class homeUrlParamHandler implements INotifyParamHandler {

    @Override
    public String getValue() {
        return CommonNotifyParam.HOMEURL.getValue();
    }

    @Override
    public Object getText(Object object) {
        String homeUrl = Config.HOME_URL();
        if(StringUtils.isNotBlank(homeUrl)) {
            if(!homeUrl.endsWith(File.separator)) {
                homeUrl += File.separator;
            }
            return homeUrl + TenantContext.get().getTenantUuid() + File.separator;
        }
        return null;
    }
}
