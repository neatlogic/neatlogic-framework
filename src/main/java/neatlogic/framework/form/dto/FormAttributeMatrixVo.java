/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

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

package neatlogic.framework.form.dto;

/**
 * @program: neatlogic
 * @description:
 * @create: 2020-03-27 15:57
 **/
public class FormAttributeMatrixVo {
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
