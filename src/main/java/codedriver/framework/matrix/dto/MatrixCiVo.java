/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.matrix.dto;

import com.alibaba.fastjson.JSONObject;

/**
 * @author linbq
 * @since 2021/11/15 15:21
 **/
public class MatrixCiVo {
    private String matrixUuid;
    private Long ciId;
    private JSONObject config;
    private String configStr;

    public MatrixCiVo() {
    }

    public MatrixCiVo(String matrixUuid, Long ciId, JSONObject config) {
        this.matrixUuid = matrixUuid;
        this.ciId = ciId;
        this.config = config;
    }

    public String getMatrixUuid() {
        return matrixUuid;
    }

    public void setMatrixUuid(String matrixUuid) {
        this.matrixUuid = matrixUuid;
    }

    public Long getCiId() {
        return ciId;
    }

    public void setCiId(Long ciId) {
        this.ciId = ciId;
    }

    public JSONObject getConfig() {
        return config;
    }

    public void setConfig(JSONObject config) {
        this.config = config;
    }

    public String getConfigStr() {
        return configStr;
    }

    public void setConfigStr(String configStr) {
        this.configStr = configStr;
    }
}
