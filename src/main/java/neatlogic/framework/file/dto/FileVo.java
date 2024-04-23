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

package neatlogic.framework.file.dto;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BaseEditorVo;
import neatlogic.framework.file.core.FileTypeHandlerFactory;
import neatlogic.framework.file.core.IFileTypeHandler;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.SnowflakeUtil;
import neatlogic.framework.util.TimeUtil;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FileVo extends BaseEditorVo {
    @EntityField(name = "附件id", type = ApiParamType.LONG)
    private Long id;
    @EntityField(name = "附件保存路径", type = ApiParamType.STRING)
    private String path;
    @EntityField(name = "附件名称", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "路径附件名称", type = ApiParamType.STRING)
    private String pathName;
    @EntityField(name = "附件大小（字节）", type = ApiParamType.INTEGER)
    private Long size;
    @EntityField(name = "附件大小", type = ApiParamType.STRING)
    private String sizeText;
    @EntityField(name = "上传用户", type = ApiParamType.STRING)
    private String userUuid;
    @EntityField(name = "上传时间", type = ApiParamType.STRING)
    private Date uploadTime;

    @JSONField(serialize = false)
    private String uniqueKey;

    @JSONField(serialize = false)
    private JSONObject uploadTimeRange;//搜索条件上传时间范围
    @EntityField(name = "附件归属", type = ApiParamType.STRING)
    private String type;
    @EntityField(name = "附件归属名称", type = ApiParamType.STRING)
    private String typeText;
    @EntityField(name = "扩展名", type = ApiParamType.STRING)
    private String ext;
    @EntityField(name = "内容类型", type = ApiParamType.STRING)
    private String contentType;
    @EntityField(name = "下载url", type = ApiParamType.STRING)
    private String url;
    @JSONField(serialize = false)
    private List<Map<String, String>> sortList;//排序设置

    public Long getId() {
        if (id == null) {
            id = SnowflakeUtil.uniqueLong();
        }
        return id;
    }

    public String getTypeText() {
        if (StringUtils.isNotBlank(type)) {
            IFileTypeHandler handler = FileTypeHandlerFactory.getHandler(type);
            if (handler != null) {
                typeText = handler.getDisplayName();
            }
        }
        return typeText;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    DecimalFormat decimalFormat = new DecimalFormat("0.##");
    String[] units = new String[]{"字节", "KB", "MB", "GB"};

    public String getSizeText() {
        if (StringUtils.isBlank(sizeText)) {
            double d = size;
            int unitindex = 0;
            while (d > 1024 && unitindex <= 3) {
                d = d / 1024;
                unitindex += 1;
            }
            sizeText = decimalFormat.format(d) + units[unitindex];
        }
        return sizeText;
    }

    public List<Map<String, String>> getSortList() {
        return sortList;
    }

    public void setSortList(List<Map<String, String>> sortList) {
        this.sortList = sortList;
    }

    public List<Long> getUploadTimeRange() {
        return TimeUtil.getTimeRangeList(this.uploadTimeRange);
    }

    public void setUploadTimeRange(JSONObject uploadTimeRange) {
        this.uploadTimeRange = uploadTimeRange;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExt() {
        if (StringUtils.isNotBlank(name) && name.contains(".")) {
            ext = name.substring(name.lastIndexOf(".") + 1);
        }
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPathName() {
        if(StringUtils.isBlank(pathName)){
            pathName = getId().toString();
        }
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }
}
