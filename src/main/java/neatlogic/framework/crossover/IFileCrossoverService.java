/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.framework.crossover;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.file.dto.FileTypeVo;
import neatlogic.framework.file.dto.FileVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IFileCrossoverService extends ICrossoverService {
    void downloadFile(JSONObject paramObj, HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * 删除文件
     * @param fileId 文件id
     * @param paramObj 其他参数
     * @throws Exception
     */
    void deleteFile(Long fileId, JSONObject paramObj) throws Exception;


    /**
     * 保存上传的文件
     * @param multipartFile 文件对象
     * @param type 文件类型
     * @param fileTypeConfigVo 文件配置
     * @param paramObj 接口入参
     * @return 文件vo
     */
    FileVo saveFile(MultipartFile multipartFile, String type, FileTypeVo fileTypeConfigVo, JSONObject paramObj) throws Exception;
}
