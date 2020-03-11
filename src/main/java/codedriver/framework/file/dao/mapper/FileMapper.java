package codedriver.framework.file.dao.mapper;

import codedriver.framework.file.dto.FileTypeVo;
import codedriver.framework.file.dto.FileVo;

public interface FileMapper {
	public FileVo getFileByUuid(String uuid);

	public FileTypeVo getFileTypeConfigByType(String name);

	public int insertFile(FileVo fileVo);
}
