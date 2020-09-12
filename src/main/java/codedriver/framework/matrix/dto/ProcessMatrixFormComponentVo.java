package codedriver.framework.matrix.dto;

/**
 * @program: codedriver
 * @description:
 * @create: 2020-03-27 15:57
 **/
public class ProcessMatrixFormComponentVo {
    private String matrixUuid;
    private String formVersionUuid;
    private String formAttributeUuid;
    private String formAttributeLabel;
    private String formName;
    private String formUuid;
    private String version;

    public String getMatrixUuid() {
        return matrixUuid;
    }

    public void setMatrixUuid(String matrixUuid) {
        this.matrixUuid = matrixUuid;
    }

    public String getFormVersionUuid() {
        return formVersionUuid;
    }

    public void setFormVersionUuid(String formVersionUuid) {
        this.formVersionUuid = formVersionUuid;
    }

    public String getFormAttributeUuid() {
		return formAttributeUuid;
	}

	public void setFormAttributeUuid(String formAttributeUuid) {
		this.formAttributeUuid = formAttributeUuid;
	}

	public String getFormAttributeLabel() {
		return formAttributeLabel;
	}

	public void setFormAttributeLabel(String formAttributeLabel) {
		this.formAttributeLabel = formAttributeLabel;
	}

	public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getFormUuid() {
        return formUuid;
    }

    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
