/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
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
