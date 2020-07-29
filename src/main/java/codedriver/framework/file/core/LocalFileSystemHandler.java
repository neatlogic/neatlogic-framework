package codedriver.framework.file.core;

import codedriver.framework.common.RootComponent;
import codedriver.framework.common.config.Config;
import codedriver.framework.file.dto.FileVo;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@RootComponent
public class LocalFileSystemHandler implements IFileStorageMediumHandler {

	public static final String NAME = "LOCAL_FILE_SYSTEM";


	@Override
	public String getName() {
		return NAME;
	}


	public String saveData(String tenantUuid, MultipartFile multipartFile, FileVo fileVo) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy" + File.separator + "MM" + File.separator + "dd");
		String filePath = tenantUuid + File.separator + format.format(new Date()) + File.separator + fileVo.getId();
		String finalPath = Config.DATA_HOME() + filePath;
		File file = new File(finalPath);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		FileOutputStream fos = new FileOutputStream(file);
		IOUtils.copyLarge(multipartFile.getInputStream(), fos);
		fos.flush();
		fos.close();
		fileVo.setPath("file:" + filePath);
		return fileVo.getPath();
	}

}
