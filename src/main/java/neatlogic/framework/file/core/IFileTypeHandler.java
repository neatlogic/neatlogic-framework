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

package neatlogic.framework.file.core;

import neatlogic.framework.file.dto.FileVo;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.multipart.MultipartFile;

public interface IFileTypeHandler {
    /**
     * 校验附件是否允许访问
     *
     * @param userUuid 用户uuid
     * @param fileVo   附件信息
     * @param jsonObj  校验所需参数
     * @return 是否允许访问
     */
    boolean valid(String userUuid, FileVo fileVo, JSONObject jsonObj) throws Exception;

    /**
     * 文件名是否唯一，如果返回true，相同名字的文件会被新文件覆盖
     *
     * @return 文件名是否唯一
     */
    default boolean isUnique() {
        return false;
    }

    /**
     * 获取唯一key
     *
     * @param key key原值
     * @return key md5 hex
     */
    String getUniqueKey(String key);

    String getName();

    String getDisplayName();

    void deleteFile(FileVo fileVo, JSONObject paramObj) throws Exception;

    /**
     * 上传前校验，执行在分析黑白名单之前
     *
     * @param jsonObj 上传参数
     */
    default boolean beforeUpload(JSONObject jsonObj) {
        return true;
    }

    /**
     * 上传完毕的后续动作
     *
     * @param fileVo  文件对象
     * @param jsonObj 传入参数
     */
    default void afterUpload(FileVo fileVo, JSONObject jsonObj) {
    }

    /**
     * 是否需要保存附件（某些上传或分析的场景可能不需要保存附件）
     *
     * @return 是否需要保存
     */
    default boolean needSave() {
        return true;
    }

    /**
     * 分析附件，一般在导入或分析的场景使用，支持流式处理附件内容，由于文件流只能读一次，所以要配合needSave=false使用
     */
    default void analyze(MultipartFile multipartFile, JSONObject paramObj) throws Exception {

    }
}
