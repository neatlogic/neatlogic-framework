package codedriver.framework.matrix.dto;

/**
 * @program: codedriver
 * @description:
 * @create: 2020-04-02 18:25
 **/
public class MatrixExternalVo {
    private String matrixUuid;
    private String integrationUuid;

    public MatrixExternalVo() {
    }

    public MatrixExternalVo(String matrixUuid, String integrationUuid) {
        this.matrixUuid = matrixUuid;
        this.integrationUuid = integrationUuid;
    }

    public String getMatrixUuid() {
        return matrixUuid;
    }

    public void setMatrixUuid(String matrixUuid) {
        this.matrixUuid = matrixUuid;
    }

	public String getIntegrationUuid() {
		return integrationUuid;
	}

	public void setIntegrationUuid(String integrationUuid) {
		this.integrationUuid = integrationUuid;
	}
}
