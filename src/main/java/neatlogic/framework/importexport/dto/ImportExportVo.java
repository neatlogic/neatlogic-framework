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

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ImportExportVo extends ImportExportBaseInfoVo {

    private static final Logger logger = LoggerFactory.getLogger(ImportExportVo.class);
    private static final long serialVersionUID = -5142624297760626822L;

    private JSONObject data;
    private List<ImportExportBaseInfoVo> dependencyBaseInfoList;

    public ImportExportVo() {
    }

    public ImportExportVo(String type, Object primaryKey, String name) {
        super(type, primaryKey, name);
    }

    public void setDataWithObject(Object obj) {
        Object jsonObj = JSONObject.toJSON(obj);
        if (jsonObj instanceof JSONObject) {
            this.data = (JSONObject) jsonObj;
        } else {
            logger.error(obj + "toJSON fail");
        }
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public List<ImportExportBaseInfoVo> getDependencyBaseInfoList() {
        return dependencyBaseInfoList;
    }

    public void setDependencyBaseInfoList(List<ImportExportBaseInfoVo> dependencyBaseInfoList) {
        this.dependencyBaseInfoList = dependencyBaseInfoList;
    }
}
