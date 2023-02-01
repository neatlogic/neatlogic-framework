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

package neatlogic.framework.file.dao.mapper;

import neatlogic.framework.file.dto.FileTypeVo;
import neatlogic.framework.file.dto.FileVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FileMapper {
    int searchFileCount(FileVo fileVo);

    List<FileVo> searchFile(FileVo fileVo);

    FileVo getFileByNameAndUniqueKey(@Param("name") String name, @Param("uniqueKey") String uniqueKey);

    FileVo getFileById(Long id);

    FileTypeVo getFileTypeConfigByType(String name);

    List<FileVo> getFileListByIdList(List<Long> idList);

    List<FileVo> getFileDetailListByIdList(List<Long> idList);

    void updateFile(FileVo fileVo);

    int insertFile(FileVo fileVo);

    void deleteFile(Long fileId);
}
