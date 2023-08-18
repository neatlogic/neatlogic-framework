package neatlogic.framework.dto.plugin.issue;


import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;

public class ProjectSearchVo {

    @EntityField(name = "同步源对象", type = ApiParamType.JSONARRAY)
    private SyncSourceVo syncSourceVo;


    public SyncSourceVo getSyncSourceVo() {
        return syncSourceVo;
    }

    public void setSyncSourceVo(SyncSourceVo syncSourceVo) {
        this.syncSourceVo = syncSourceVo;
    }
}
