package codedriver.framework.file.core;

import codedriver.framework.common.RootComponent;
import codedriver.framework.common.config.Config;
import codedriver.framework.exception.file.FilePathIllegalException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@RootComponent
public class LocalFileSystemHandler implements IFileStorageMediumHandler {

	public static final String NAME = "FILE";


	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String saveData(String tenantUuid, InputStream inputStream, Long fileId,String contentType,String fileType) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy" + File.separator + "MM" + File.separator + "dd");
		String filePath = tenantUuid + File.separator + fileType + File.separator + format.format(new Date()) + File.separator + fileId;
		String finalPath = Config.DATA_HOME() + filePath;
		File file = new File(finalPath);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		FileOutputStream fos = new FileOutputStream(file);
		IOUtils.copyLarge(inputStream, fos);
		fos.flush();
		fos.close();
//		fileVo.setPath("file:" + filePath);
		return LocalFileSystemHandler.NAME.toLowerCase() + ":" + filePath;
	}

	@Override
	public InputStream getData(String path) throws Exception {
		InputStream in = null;
		File file = new File(Config.DATA_HOME() + path.substring(5));
		if (file.exists() && file.isFile()) {
			in = new FileInputStream(file);
		}
		return in;
	}

	@Override
	public void deleteData(String filePath) throws Exception {
		if(StringUtils.isNotBlank(filePath) && filePath.startsWith(LocalFileSystemHandler.NAME.toLowerCase() + ":")){
			File file = new File(Config.DATA_HOME() + filePath.substring(5));
			if(file.exists()){
				file.delete();
			}
		}else{
			throw new FilePathIllegalException(filePath);
		}
	}
}
