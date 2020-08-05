package codedriver.framework.restful.audit;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.util.FileUtil;
import codedriver.framework.file.core.LocalFileSystemHandler;
import codedriver.framework.file.dao.mapper.FileMapper;
import codedriver.framework.file.dto.FileVo;
import codedriver.framework.minio.core.MinioManager;
import codedriver.framework.restful.dao.mapper.ApiMapper;
import codedriver.framework.restful.dto.ApiAuditVo;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
			StringBuilder sb = new StringBuilder();
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
			sb.append("error>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			sb.append("\n");
			if(StringUtils.isNotBlank(apiAuditVo.getError())){
				int offset = apiAuditVo.getError().getBytes(StandardCharsets.UTF_8).length;
				apiAuditVo.setErrorFilePath("?startIndex=" + sb.toString().getBytes(StandardCharsets.UTF_8).length + "&offset=" + offset);
				sb.append(apiAuditVo.getError());
				sb.append("\n");
			}
			sb.append("error<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			sb.append("\n");
			sb.append("result>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			sb.append("\n");

			/**
			 * 先记录下截止到error结束，result定界符开始时的文件长度
			 */
			long lengthWithoutResult = sb.toString().getBytes(StandardCharsets.UTF_8).length;

			/**
			 * 由于result可能比较大，故最后写入result，以方便计算偏移量
			 */
			if(apiAuditVo.getResult() != null && StringUtils.isNotBlank(apiAuditVo.getResult().toString())){
				sb.append(apiAuditVo.getResult());
				sb.append("\n");
			}
			sb.append("result<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			InputStream inputStream = IOUtils.toInputStream(sb.toString(), StandardCharsets.UTF_8);
			String filePath = null;
			try {
				filePath = FileUtil.saveData(MinioManager.NAME,tenantUuid,inputStream,fileVo.getId(),"text/plain","API_AUDIT");
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				try {
					filePath = FileUtil.saveData(LocalFileSystemHandler.NAME,tenantUuid,inputStream,fileVo.getId(),"text/plain","API_AUDIT");
				} catch (Exception e1) {
					logger.error(e1.getMessage(),e1);
					e1.printStackTrace();
				}
			}

			if(StringUtils.isNotBlank(filePath)){
				long length = FileUtil.getDataLength(filePath);
				fileVo.setPath(filePath);
				fileVo.setSize(length);
				fileVo.setContentType("text/plain");
				fileVo.setType("API_AUDIT");
				fileMapper.insertFile(fileVo);

				/** 记录文件路径和偏移量，插入api_audit表 */
				if(StringUtils.isNotBlank(apiAuditVo.getParamFilePath())){
					apiAuditVo.setParamFilePath(filePath + apiAuditVo.getParamFilePath());
				}
				if(apiAuditVo.getResult() != null && StringUtils.isNotBlank(apiAuditVo.getResult().toString())){
					/**
					 * 计算result在文件中的起始位置和偏移量
					 * 偏移量 = 文件总长度 - 截止到result开始定界符的长度 - result结束定界符的长度
					 */
					long resultOffset = length - lengthWithoutResult - "result<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<".getBytes(StandardCharsets.UTF_8).length -1;
					String resultOffsetStr = "?startIndex=" + lengthWithoutResult + "&offset=" + resultOffset;
					apiAuditVo.setResultFilePath(filePath + resultOffsetStr);
				}
				if(StringUtils.isNotBlank(apiAuditVo.getErrorFilePath())){
					apiAuditVo.setErrorFilePath(filePath + apiAuditVo.getErrorFilePath());
				}
			}
			apiMapper.insertApiAudit(apiAuditVo);
		}
	}

}
