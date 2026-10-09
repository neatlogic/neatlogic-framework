package neatlogic.framework.auth.label;

import neatlogic.framework.auth.core.AuthBase;
import neatlogic.framework.common.constvalue.systemuser.ISystemUser;
import neatlogic.framework.common.constvalue.systemuser.SystemUser;

import java.util.List;

/** 允许下载当前租户内所有附件，不授予附件删除或其他管理权限。系统用户通过默认授权获得该能力，普通用户可通过页面授权获得相同能力。 */
public class FILE_DOWNLOAD extends AuthBase {
    @Override
    public String getAuthDisplayName() {
        return "auth.file_download.name";
    }

    @Override
    public String getAuthIntroduction() {
        return "auth.file_download.description";
    }

    @Override
    public String getAuthGroup() {
        return "framework";
    }

    @Override
    public Integer getSort() {
        return 25;
    }

    /** 为内置后台执行身份声明默认授权，权限校验不依赖调用者身份。 */
    @Override
    public List<ISystemUser> getDefaultSystemUserList() {
        return List.of(SystemUser.SYSTEM);
    }
}
