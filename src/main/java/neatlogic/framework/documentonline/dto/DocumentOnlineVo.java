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

import java.io.Serializable;
import java.util.List;

public class DocumentOnlineVo implements Serializable {

    private static final long serialVersionUID = -928973151322839787L;

    @EntityField(name = "common.filename", type = ApiParamType.STRING)
    private String fileName;
    @EntityField(name = "common.filepath", type = ApiParamType.STRING)
    private String filePath;
    @EntityField(name = "common.upwardnamelist", type = ApiParamType.JSONARRAY)
    private List<String> upwardNameList;
    @EntityField(name = "common.content", type = ApiParamType.STRING)
    private String content;
    @EntityField(name = "common.anchorpoint", type = ApiParamType.STRING)
    private String anchorPoint;
    @EntityField(name = "common.configlist", type = ApiParamType.JSONARRAY)
    private List<DocumentOnlineConfigVo> configList;
    @EntityField(name = "common.prefix", type = ApiParamType.STRING)
    private Integer prefix;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<String> getUpwardNameList() {
        return upwardNameList;
    }

    public void setUpwardNameList(List<String> upwardNameList) {
        this.upwardNameList = upwardNameList;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAnchorPoint() {
        return anchorPoint;
    }

    public void setAnchorPoint(String anchorPoint) {
        this.anchorPoint = anchorPoint;
    }

    public List<DocumentOnlineConfigVo> getConfigList() {
        return configList;
    }

    public void setConfigList(List<DocumentOnlineConfigVo> configList) {
        this.configList = configList;
    }

    public Integer getPrefix() {
        return prefix;
    }

    public void setPrefix(Integer prefix) {
        this.prefix = prefix;
    }
}
