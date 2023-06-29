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

package neatlogic.framework.documentonline.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

public class DocumentOnlineConfigVo implements Serializable {

    private static final long serialVersionUID = -928973151356839787L;
    @EntityField(name = "文件路径", type = ApiParamType.STRING)
    private String filePath;
    @EntityField(name = "模块组标识", type = ApiParamType.STRING)
    private String moduleGroup;
    @EntityField(name = "菜单标识", type = ApiParamType.STRING)
    private String menu;
    @EntityField(name = "锚点", type = ApiParamType.STRING)
    private String anchorPoint;
    @EntityField(name = "配置信息来源", type = ApiParamType.STRING)
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
