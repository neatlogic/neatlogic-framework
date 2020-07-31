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
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.Charset;

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
			apiMapper.insertApiAudit(apiAuditVo);
//			if (StringUtils.isNotBlank(apiAuditVo.getErrorHash())) {
//				apiMapper.replaceApiAuditDetail(apiAuditVo.getErrorHash(), apiAuditVo.getError());
//			}
//			if (StringUtils.isNotBlank(apiAuditVo.getResultHash())) {
//				apiMapper.replaceApiAuditDetail(apiAuditVo.getResultHash(), JSON.toJSONString(apiAuditVo.getResult()));
//			}
//			if (StringUtils.isNotBlank(apiAuditVo.getParamHash())) {
//				apiMapper.replaceApiAuditDetail(apiAuditVo.getParamHash(), apiAuditVo.getParam());
//			}
			if(StringUtils.isNotBlank(apiAuditVo.getAuditDetailHash())){
				/**
				 * 创建FileVo对象，生成文件流，调用FileUtil.saveData存储文件并得到filePath
				 * 将filePath装入FileVo对象，插入file表
				 * 将fileId赋值给apiAuditVo的detailFileId字段，插入api_audit_detail表
 				 */
				String tenantUuid = TenantContext.get().getTenantUuid();
				FileVo fileVo = new FileVo();
				fileVo.setName(fileVo.getId().toString());
				fileVo.setUserUuid(apiAuditVo.getUserUuid());
				/**
				 * 组装文件内容JSON
				 */
				JSONObject auditDetail = new JSONObject();
				auditDetail.put("auditDetailHash",apiAuditVo.getAuditDetailHash());
				auditDetail.put("param",apiAuditVo.getParam());
				auditDetail.put("result",apiAuditVo.getResult());
				auditDetail.put("error",apiAuditVo.getError());
//				ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(auditDetail.toJSONString().getBytes());
				InputStream inputStream = IOUtils.toInputStream(auditDetail.toJSONString(), Charset.forName("UTF-8"));
				String filePath = null;
				try {
					// TODO fileType待定，存储介质待定
					filePath = FileUtil.saveData(MinioManager.NAME,tenantUuid,inputStream,fileVo.getId(),"text/plain","API_AUDIT");
				} catch (Exception e) {
					try {
						filePath = FileUtil.saveData(LocalFileSystemHandler.NAME,tenantUuid,inputStream,fileVo.getId(),"text/plain","API_AUDIT");
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
				fileVo.setPath(filePath);
				fileVo.setSize(new Long(auditDetail.toJSONString().getBytes().length));
				fileVo.setContentType("text/plain");
				// TODO fileType待定
				fileVo.setType("API_AUDIT");
				fileMapper.insertFile(fileVo);

				apiAuditVo.setDetailFileId(fileVo.getId());
				apiMapper.insertApiAuditDetail(apiAuditVo.getAuditDetailHash(),apiAuditVo.getDetailFileId());
				try {
					InputStream data = FileUtil.getData(filePath);
					String s = IOUtils.toString(data,Charset.forName("UTF-8"));
					System.out.println(s);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}

}
