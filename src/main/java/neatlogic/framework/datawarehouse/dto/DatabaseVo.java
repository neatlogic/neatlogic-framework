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
