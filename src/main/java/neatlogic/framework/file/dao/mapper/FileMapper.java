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
