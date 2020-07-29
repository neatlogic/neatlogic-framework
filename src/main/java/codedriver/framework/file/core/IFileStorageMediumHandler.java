package codedriver.framework.file.core;

import codedriver.framework.file.dto.FileVo;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;

public interface IFileStorageMediumHandler {

	public String getName();

	public String saveData(String tenantUuid, MultipartFile multipartFile, FileVo fileVo) throws Exception;
}
