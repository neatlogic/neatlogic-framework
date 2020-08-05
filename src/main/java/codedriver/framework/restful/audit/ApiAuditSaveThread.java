package codedriver.framework.restful.audit;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.config.Config;
import codedriver.framework.common.util.FileUtil;
import codedriver.framework.file.core.LocalFileSystemHandler;
import codedriver.framework.file.dao.mapper.FileMapper;
import codedriver.framework.file.dto.FileVo;
import codedriver.framework.restful.dao.mapper.ApiMapper;
import codedriver.framework.restful.dto.ApiAuditVo;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

@Service
public class ApiAuditSaveThread extends CodeDriverThread {
	Logger logger = LoggerFactory.getLogger(ApiAuditSaveThread.class);

	private static ApiMapper apiMapper;

	private static FileMapper fileMapper;

	@Autowired
	public void setFileMapper(FileMapper _fileMapper) {
		fileMapper = _fileMapper;
	}

	@Autowired
	public void setApiMapper(ApiMapper _apiMapper) {
		apiMapper = _apiMapper;
	}

	private ApiAuditVo apiAuditVo;

	public ApiAuditSaveThread(ApiAuditVo _apiAuditVo) {
		apiAuditVo = _apiAuditVo;
	}

	public ApiAuditSaveThread() {

	}

	@Override
	protected void execute() {
		if (apiAuditVo != null) {
			/**
			 * 创建FileVo对象，生成文件流，调用FileUtil.saveData存储文件并得到filePath
			 * 将filePath装入FileVo对象，插入file表
			 * 将filePath和各自内容的坐标、偏移量赋给apiAuditVo，插入api_audit表
 			 */
			String tenantUuid = TenantContext.get().getTenantUuid();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
			FileVo fileVo = new FileVo();
			fileVo.setName("API_AUDIT-" + sdf.format(apiAuditVo.getStartTime()));
			fileVo.setUserUuid(apiAuditVo.getUserUuid());
			/**
			 * 组装文件内容JSON并且计算文件中每一块内容的开始坐标和偏移量
			 * 例如参数的开始坐标为"param>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"的字节数
			 * 偏移量为apiAuditVo.getParam()的字节数(注意一定要用UTF-8格式，否则计算出来的偏移量不对)
			 */
			StringBuffer sb = new StringBuffer();
			sb.append("param>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			sb.append("\n");
			if(StringUtils.isNotBlank(apiAuditVo.getParam())){
				int offset = apiAuditVo.getParam().getBytes(StandardCharsets.UTF_8).length;
				apiAuditVo.setParamFilePath("?startIndex=" + sb.toString().getBytes(StandardCharsets.UTF_8).length + "&offset=" + offset);
				sb.append(apiAuditVo.getParam());
				sb.append("\n");
			}
			sb.append("param<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			sb.append("\n");
//			sb.append("result>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//			sb.append("\n");
//			if(apiAuditVo.getResult() != null && StringUtils.isNotBlank(apiAuditVo.getResult().toString())){
//				int offset = apiAuditVo.getResult().toString().getBytes(StandardCharsets.UTF_8).length;
//				apiAuditVo.setResultFilePath("?startIndex=" + sb.toString().getBytes(StandardCharsets.UTF_8).length + "&offset=" + offset);
//				sb.append(apiAuditVo.getResult());
//				sb.append("\n");
//			}
//			sb.append("result<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
//			sb.append("\n");
			sb.append("error>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			sb.append("\n");
			if(StringUtils.isNotBlank(apiAuditVo.getError())){
				int offset = apiAuditVo.getError().getBytes(StandardCharsets.UTF_8).length;
				apiAuditVo.setErrorFilePath("?startIndex=" + sb.toString().getBytes(StandardCharsets.UTF_8).length + "&offset=" + offset);
				sb.append(apiAuditVo.getError());
				sb.append("\n");
			}
			sb.append("error>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			sb.append("\n");
			sb.append("result>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			sb.append("\n");

			long lengthWithoutResult = sb.toString().getBytes(StandardCharsets.UTF_8).length;

			if(apiAuditVo.getResult() != null && StringUtils.isNotBlank(apiAuditVo.getResult().toString())){
				sb.append(apiAuditVo.getResult());
				sb.append("\n");
			}
			sb.append("result<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			InputStream inputStream = IOUtils.toInputStream(sb.toString(), StandardCharsets.UTF_8);
			String filePath = null;
			try {
				// TODO fileType待定
				filePath = FileUtil.saveData(LocalFileSystemHandler.NAME,tenantUuid,inputStream,fileVo.getId(),"text/plain","API_AUDIT");
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
			fileVo.setPath(filePath);
			fileVo.setSize(new Long(sb.toString().getBytes(StandardCharsets.UTF_8).length));
			fileVo.setContentType("text/plain");
			// TODO fileType待定
			fileVo.setType("API_AUDIT");
			fileMapper.insertFile(fileVo);

			System.out.println("获取长度前：" + System.currentTimeMillis());
			File file = new File(Config.DATA_HOME() + filePath.substring(5));
			long length = file.length();
			System.out.println("获取长度后：" + System.currentTimeMillis());
			long resultOffset = length - lengthWithoutResult - "result<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<".getBytes(StandardCharsets.UTF_8).length -1;
			String resultOffsetStr = "?startIndex=" + lengthWithoutResult + "&offset=" + resultOffset;

			/** 记录文件路径和偏移量，插入api_audit表 */
			if(StringUtils.isNotBlank(apiAuditVo.getParamFilePath())){
				apiAuditVo.setParamFilePath(filePath + apiAuditVo.getParamFilePath());
			}
			if(StringUtils.isNotBlank(resultOffsetStr)){
				apiAuditVo.setResultFilePath(filePath + resultOffsetStr);
			}
			if(StringUtils.isNotBlank(apiAuditVo.getErrorFilePath())){
				apiAuditVo.setErrorFilePath(filePath + apiAuditVo.getErrorFilePath());
			}
			apiMapper.insertApiAudit(apiAuditVo);

		}
	}

}
