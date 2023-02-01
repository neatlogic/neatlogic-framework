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

package neatlogic.module.framework.matrix.file.handler;

import neatlogic.framework.common.util.FileUtil;
import neatlogic.framework.file.core.FileTypeHandlerBase;
import neatlogic.framework.file.dto.FileVo;
import neatlogic.framework.matrix.exception.MatrixViewSqlIrregularException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.dom4j.DocumentHelper;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class MatrixViewFileHandler extends FileTypeHandlerBase {

    @Override
    public boolean valid(String userUuid, FileVo fileVo, JSONObject jsonObj) {
        return true;
    }

    @Override
    public String getDisplayName() {
        return "矩阵视图配置文件";
    }

    @Override
    protected boolean myDeleteFile(FileVo fileVo, JSONObject paramObj) {
        return true;
    }

    @Override
    public void afterUpload(FileVo fileVo, JSONObject jsonObj) {
        try {
            String xml = IOUtils.toString(FileUtil.getData(fileVo.getPath()), StandardCharsets.UTF_8);
            DocumentHelper.parseText(xml);
        } catch (Exception e) {
            throw new MatrixViewSqlIrregularException();
        }
    }

    @Override
    public String getName() {
        return "MATRIXVIEW";
    }

}
