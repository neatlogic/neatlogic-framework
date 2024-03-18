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

package neatlogic.framework.documentonline.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

public class DocumentOnlineConfigVo implements Serializable {

    private static final long serialVersionUID = -928973151356839787L;
    @EntityField(name = "common.filepath", type = ApiParamType.STRING)
    private String filePath;
    @EntityField(name = "common.modulegroup", type = ApiParamType.STRING)
    private String moduleGroup;
    @EntityField(name = "common.menu", type = ApiParamType.STRING)
    private String menu;
    @EntityField(name = "common.anchorpoint", type = ApiParamType.STRING)
    private String anchorPoint;
    @EntityField(name = "common.source", type = ApiParamType.STRING)
    private String source;

    public DocumentOnlineConfigVo() {}

    public DocumentOnlineConfigVo(DocumentOnlineConfigVo configVo) {
        this.filePath = configVo.getFilePath();
        this.moduleGroup = configVo.getModuleGroup();
        this.menu = configVo.getMenu();
        this.anchorPoint = configVo.getAnchorPoint();
        this.source = configVo.getSource();
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getModuleGroup() {
        return moduleGroup;
    }

    public void setModuleGroup(String moduleGroup) {
        this.moduleGroup = moduleGroup;
    }

    public String getMenu() {
        if (menu == null) {
            menu = StringUtils.EMPTY;
        }
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getAnchorPoint() {
        return anchorPoint;
    }

    public void setAnchorPoint(String anchorPoint) {
        this.anchorPoint = anchorPoint;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentOnlineConfigVo)) return false;
        DocumentOnlineConfigVo that = (DocumentOnlineConfigVo) o;
        return filePath.equals(that.filePath) && moduleGroup.equals(that.moduleGroup) && menu.equals(that.menu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filePath, moduleGroup, menu);
    }
}
