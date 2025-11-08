/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
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
