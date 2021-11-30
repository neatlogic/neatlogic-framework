/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.matrix.dto;

/**
 * @author linbq
 * @since 2021/11/30 15:32
 **/
public class MatrixCustomViewVo {
    private String matrixUuid;
    private Long customViewId;

    public MatrixCustomViewVo() {

    }
    public MatrixCustomViewVo(String matrixUuid, Long customViewId) {
        this.matrixUuid = matrixUuid;
        this.customViewId = customViewId;
    }

    public String getMatrixUuid() {
        return matrixUuid;
    }

    public void setMatrixUuid(String matrixUuid) {
        this.matrixUuid = matrixUuid;
    }

    public Long getCustomViewId() {
        return customViewId;
    }

    public void setCustomViewId(Long customViewId) {
        this.customViewId = customViewId;
    }
}
