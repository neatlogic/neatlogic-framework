/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

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
