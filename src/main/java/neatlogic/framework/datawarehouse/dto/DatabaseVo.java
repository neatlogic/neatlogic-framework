/*
 * Copyright (C) 2025  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.datawarehouse.dto;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.file.dto.FileVo;

import java.util.List;

public class DatabaseVo extends BasePageVo {
    private Long id;
    private String name;
    private String type;
    private JSONObject config;
    private List<Long> fileIdList;
    private List<FileVo> fileList;
    @JSONField(serialize = false)
    private String configStr;
    @JSONField(serialize = false)
    private String fileIdListStr;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JSONObject getConfig() {
        if (config == null && configStr != null) {
            try {
                config = JSONObject.parseObject(configStr);
            } catch (Exception ignored) {

            }
        }
        return this.config;
    }

    public void setConfig(JSONObject config) {
        this.config = config;
    }

    public String getConfigStr() {
        if (config != null) {
            configStr = config.toJSONString();
        }
        return configStr;
    }

    public void setConfigStr(String configStr) {
        this.configStr = configStr;
    }

    public List<Long> getFileIdList() {
        if (fileIdList == null && fileIdListStr != null) {
            try {
                fileIdList = JSONArray.parseArray(fileIdListStr, Long.class);
            } catch (Exception ignored) {

            }
        }
        return fileIdList;
    }

    public void setFileIdList(List<Long> fileIdList) {
        this.fileIdList = fileIdList;
    }

    public List<FileVo> getFileList() {
        return fileList;
    }

    public void setFileList(List<FileVo> fileList) {
        this.fileList = fileList;
    }

    public String getFileIdListStr() {
        if (fileIdListStr == null && fileIdList != null) {
            fileIdListStr = JSONArray.toJSONString(fileIdList);
        }
        return fileIdListStr;
    }

    public void setFileIdListStr(String fileIdListStr) {
        this.fileIdListStr = fileIdListStr;
    }
}
