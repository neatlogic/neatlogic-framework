/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
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
