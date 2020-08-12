package codedriver.framework.util;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.audit.AuditVoHandler;
import codedriver.framework.common.util.FileUtil;
import codedriver.framework.exception.file.FilePathIllegalException;
import codedriver.framework.file.core.LocalFileSystemHandler;
import codedriver.framework.file.core.MinioFileSystemHandler;
import codedriver.framework.restful.dto.ApiAuditVo;
import com.alibaba.fastjson.JSON;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

public class AuditUtil {

	/** 查看审计记录时可显示的最大字节数，超过此数需要下载文件后查看 */
	public final static long maxFileSize = 1024 * 1024;

	private static Logger logger = LoggerFactory.getLogger(AuditUtil.class);

	public static void saveAuditContent(AuditVoHandler vo,String fileType){
		/**
		 * 组装文件内容JSON并且计算文件中每一块内容的开始坐标和偏移量
		 * 例如参数的开始坐标为"param>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"的字节数
		 * 偏移量为apiAuditVo.getParam()的字节数(注意一定要用UTF-8格式，否则计算出来的偏移量不对)
		 */
		StringBuilder sb = new StringBuilder();
		sb.append("param>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		sb.append("\n");
		if(StringUtils.isNotBlank(vo.getParam())){
			int offset = vo.getParam().getBytes(StandardCharsets.UTF_8).length;
			vo.setParamFilePath("?startIndex=" + sb.toString().getBytes(StandardCharsets.UTF_8).length + "&offset=" + offset);
			sb.append(vo.getParam());
			sb.append("\n");
		}
		sb.append("param<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		sb.append("\n");
		sb.append("error>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		sb.append("\n");
		if(StringUtils.isNotBlank(vo.getError())){
			int offset = vo.getError().getBytes(StandardCharsets.UTF_8).length;
			vo.setErrorFilePath("?startIndex=" + sb.toString().getBytes(StandardCharsets.UTF_8).length + "&offset=" + offset);
			sb.append(vo.getError());
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
		 * 用一个单独的流存result，避免append到StringBuilder后增加内存负担
		 * 然后利用SequenceInputStream合并两个流
		 */
		InputStream resultInputStream = null;
		String resultStr = null;
		if(vo.getResult() != null && StringUtils.isNotBlank(resultStr = JSON.toJSON(vo.getResult()).toString())){
			resultInputStream = IOUtils.toInputStream(resultStr, StandardCharsets.UTF_8);
		}else{
			sb.append("result<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		}

		SequenceInputStream sis = null;
		Vector<InputStream> ins = new Vector<>();
		if(resultInputStream != null){
			ins.add(IOUtils.toInputStream(sb.toString(), StandardCharsets.UTF_8));
			ins.add(resultInputStream);
			ins.add(IOUtils.toInputStream("\nresult<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<",StandardCharsets.UTF_8));
		}else{
			ins.add(IOUtils.toInputStream(sb.toString(), StandardCharsets.UTF_8));
		}
		sis = new SequenceInputStream(ins.elements());

		String filePath = null;
		try {
			filePath = FileUtil.saveData(MinioFileSystemHandler.NAME, TenantContext.get().getTenantUuid(),sis, SnowflakeUtil.uniqueLong(),"text/plain",fileType);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			try {
				filePath = FileUtil.saveData(LocalFileSystemHandler.NAME,TenantContext.get().getTenantUuid(),sis,SnowflakeUtil.uniqueLong(),"text/plain",fileType);
			} catch (Exception e1) {
				logger.error(e1.getMessage(),e1);
				e1.printStackTrace();
			}
		}finally {
			try {
				if(sis != null){
					sis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(StringUtils.isNotBlank(filePath)){
			long length = 0;
			try {
				length = FileUtil.getDataLength(filePath);
			} catch (Exception e) {
				logger.error("获取数据长度失败：" + filePath);
				e.printStackTrace();
			}
			if(length != 0){

				/** 记录文件路径和偏移量*/
				if(StringUtils.isNotBlank(vo.getParamFilePath())){
					vo.setParamFilePath(filePath + vo.getParamFilePath());
				}
				if(StringUtils.isNotBlank(resultStr)){
					/**
					 * 计算result在文件中的起始位置和偏移量
					 * 偏移量 = 文件总长度 - 截止到result开始定界符的长度 - result结束定界符的长度
					 */
					long resultOffset = length - lengthWithoutResult - "result<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<".getBytes(StandardCharsets.UTF_8).length -1;
					String resultOffsetStr = "?startIndex=" + lengthWithoutResult + "&offset=" + resultOffset;
					vo.setResultFilePath(filePath + resultOffsetStr);
				}
				if(StringUtils.isNotBlank(vo.getErrorFilePath())){
					vo.setErrorFilePath(filePath + vo.getErrorFilePath());
				}
			}
		}
	}

	public static String getAuditDetail(String filePath){
		if(StringUtils.isBlank(filePath)){
			throw new FilePathIllegalException("文件路径不能为空");
		}
		if(!filePath.contains("?") || !filePath.contains("&") || !filePath.contains("=")){
			throw new FilePathIllegalException("文件路径格式错误");
		}
		String result = null;
		String path = filePath.split("\\?")[0];
		String[] indexs = filePath.split("\\?")[1].split("&");
		Long startIndex = Long.parseLong(indexs[0].split("=")[1]);
		Long offset = Long.parseLong(indexs[1].split("=")[1]);

		InputStream in = null;
		try {
			in = FileUtil.getData(path);
			if(in != null){
				/**
				 * 如果偏移量大于最大字节数限制，那么就只截取最大字节数长度的数据
				 */
				int buffSize = 0;
				if(offset > ApiAuditVo.maxFileSize){
					buffSize = (int)ApiAuditVo.maxFileSize;
				}else{
					buffSize = offset.intValue();
				}

				in.skip(startIndex);
				byte[] buff = new byte[buffSize];
				in.read(buff);

				result = new String(buff,StandardCharsets.UTF_8);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}

	public static void downLoadAuditDetail(HttpServletRequest request, HttpServletResponse response, String filePath) throws IOException {
		if(StringUtils.isBlank(filePath)){
			throw new FilePathIllegalException("文件路径不能为空");
		}
		if(!filePath.contains("?") || !filePath.contains("&") || !filePath.contains("=")){
			throw new FilePathIllegalException("文件路径格式错误");
		}
		String path = filePath.split("\\?")[0];
		String[] indexs = filePath.split("\\?")[1].split("&");
		Long startIndex = Long.parseLong(indexs[0].split("=")[1]);
		Long offset = Long.parseLong(indexs[1].split("=")[1]);

		InputStream in = null;
		try {
			in = FileUtil.getData(path);
			if(in != null){
				in.skip(startIndex);

				String fileNameEncode = "API_AUDIT.log";
				Boolean flag = request.getHeader("User-Agent").indexOf("Gecko") > 0;
				if (request.getHeader("User-Agent").toLowerCase().indexOf("msie") > 0 || flag) {
					fileNameEncode = URLEncoder.encode(fileNameEncode, "UTF-8");// IE浏览器
				} else {
					fileNameEncode = new String(fileNameEncode.getBytes(StandardCharsets.UTF_8), "ISO8859-1");
				}
				response.setContentType("aplication/x-msdownload;charset=utf-8");
				response.setHeader("Content-Disposition", "attachment;fileName=\"" + fileNameEncode + "\"");
				OutputStream os = response.getOutputStream();

				byte[] buff = new byte[(int) ApiAuditVo.maxFileSize];
				int len = 0;
				long endPoint = 0;
				while((len = in.read(buff)) != -1){
					/**
					 * endPoint用来记录累计读取到的字节数
					 * 如果大于偏移量，说明实际读到的数据超过了需要的数据
					 * 那么就需要减掉多读出来的数据
					 */
					endPoint += len;
					if(endPoint > offset){
						len = (int)(len - (endPoint - offset));
					}
					os.write(buff,0,len);
					os.flush();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(in != null){
				in.close();
			}
		}
	}
}
