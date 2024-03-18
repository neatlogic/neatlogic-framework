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
