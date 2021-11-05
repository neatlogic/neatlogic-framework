package codedriver.framework.dto.runner;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;

public class NetworkVo {

    @EntityField(name = "网段", type = ApiParamType.STRING)
    private String networkIp;
    @EntityField(name = "掩码", type = ApiParamType.INTEGER)
    private Integer mask;

    public String getNetworkIp() {
        return networkIp;
    }

    public void setNetworkIp(String networkIp) {
        this.networkIp = networkIp;
    }

    public Integer getMask() {
        return mask;
    }

    public void setMask(Integer mask) {
        this.mask = mask;
    }
}