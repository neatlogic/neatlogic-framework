/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.importexport.dto;

import java.util.List;

public class ImportDependencyTypeVo {

    private String value;
    private String text;
    private Boolean checkedAll;
    private List<ImportDependencyOptionVo> optionList;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getCheckedAll() {
        return checkedAll;
    }

    public void setCheckedAll(Boolean checkedAll) {
        this.checkedAll = checkedAll;
    }

    public List<ImportDependencyOptionVo> getOptionList() {
        return optionList;
    }

    public void setOptionList(List<ImportDependencyOptionVo> optionList) {
        this.optionList = optionList;
    }
}
