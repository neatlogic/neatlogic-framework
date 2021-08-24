package codedriver.framework.file.dao.mapper;

import java.util.List;

import codedriver.framework.file.dto.FileTypeVo;
import codedriver.framework.file.dto.FileVo;

public interface FileMapper {
	FileVo getFileById(Long id);

	FileTypeVo getFileTypeConfigByType(String name);

	List<FileVo> getFileListByIdList(List<Long> idList);

	List<FileVo> getFileListByProcessTaskId(Long processTaskId);

	int insertFile(FileVo fileVo);

	void deleteFile(Long fileId);
}
