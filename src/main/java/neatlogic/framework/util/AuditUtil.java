/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.util;

import com.alibaba.fastjson.JSON;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.audit.AuditVoHandler;
import neatlogic.framework.common.util.FileUtil;
import neatlogic.framework.exception.file.FilePathIllegalException;
import neatlogic.framework.restful.dao.mapper.ApiAuditMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class AuditUtil {

    private static ApiAuditMapper apiAuditMapper;

    @Autowired
    public void setApiMapper(ApiAuditMapper _apiAuditMapper) {
        apiAuditMapper = _apiAuditMapper;
    }

    /*查看审计记录时可显示的最大字节数，超过此数需要下载文件后查看*/
    public final static long maxFileSize = 1024 * 1024;

    /**
     * 可导出的最大字节数
     **/
    public final static long maxExportSize = 1024;

    private static final Logger logger = LoggerFactory.getLogger(AuditUtil.class);

    public static void saveAuditDetail(AuditVoHandler vo, String fileType) {
        /*
          组装文件内容JSON并且计算文件中每一块内容的开始坐标和偏移量
          例如参数的开始坐标为"param>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"的字节数
          偏移量为apiAuditVo.getParam()的字节数(注意一定要用UTF-8格式，否则计算出来的偏移量不对)
         */
        String paramFilePath = null;
        String resultFilePath = null;
        String errorFilePath = null;
        StringBuilder sb = new StringBuilder();
        sb.append("param>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        sb.append("\n");
        if (StringUtils.isNotBlank(vo.getParam()) && !vo.getParam().equals("{}") && !vo.getParam().equals("[]")) {
            int offset = vo.getParam().getBytes(StandardCharsets.UTF_8).length;
            paramFilePath = "?startIndex=" + sb.toString().getBytes(StandardCharsets.UTF_8).length + "&offset=" + offset;
            sb.append(vo.getParam());
            sb.append("\n");
        }
        sb.append("param<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        sb.append("\n");
        sb.append("result>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        sb.append("\n");
        String resultStr;
        //System.out.println(vo.getResult());
        if (vo.getResult() != null && StringUtils.isNotBlank(resultStr = JSON.toJSON(vo.getResult()).toString())) {
            int offset = resultStr.getBytes(StandardCharsets.UTF_8).length;
            resultFilePath = "?startIndex=" + sb.toString().getBytes(StandardCharsets.UTF_8).length + "&offset=" + offset;
            sb.append(resultStr);
            sb.append("\n");
        }
        sb.append("result<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        sb.append("\n");
        sb.append("error>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        sb.append("\n");
        if (StringUtils.isNotBlank(vo.getError())) {
            int offset = vo.getError().getBytes(StandardCharsets.UTF_8).length;
            errorFilePath = "?startIndex=" + sb.toString().getBytes(StandardCharsets.UTF_8).length + "&offset=" + offset;
            sb.append(vo.getError());
            sb.append("\n");
        }
        sb.append("error<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        String fileHash = DigestUtils.md5DigestAsHex(sb.toString().getBytes());
        String filePath;
        filePath = apiAuditMapper.getAuditFileByHash(fileHash);
        /* 如果在audit_file表中找到文件路径，说明此次请求与之前某次请求完全一致，则不再重复生成日志文件 */
        if (StringUtils.isBlank(filePath)) {
            InputStream inputStream = IOUtils.toInputStream(sb.toString(), StandardCharsets.UTF_8);
            try {
                filePath = FileUtil.saveData(TenantContext.get().getTenantUuid(), inputStream, fileHash, "text/plain", fileType);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            } finally {
                if (StringUtils.isNotBlank(filePath)) {
                    apiAuditMapper.insertAuditFile(fileHash, filePath);
                }
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        if (StringUtils.isNotBlank(filePath)) {
            vo.setParamFilePath(StringUtils.isNotBlank(paramFilePath) ? (filePath + paramFilePath) : null);
            vo.setResultFilePath(StringUtils.isNotBlank(resultFilePath) ? (filePath + resultFilePath) : null);
            vo.setErrorFilePath(StringUtils.isNotBlank(errorFilePath) ? (filePath + errorFilePath) : null);
        }
    }

    public static String getAuditDetail(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            throw new FilePathIllegalException("文件路径不能为空");
        }
        if (!filePath.contains("?") || !filePath.contains("&") || !filePath.contains("=")) {
            throw new FilePathIllegalException("文件路径格式错误");
        }
        String result = null;
        String path = filePath.split("\\?")[0];
        String[] indexs = filePath.split("\\?")[1].split("&");
        long startIndex = Long.parseLong(indexs[0].split("=")[1]);
        long offset = Long.parseLong(indexs[1].split("=")[1]);

        InputStream in = null;
        try {
            in = FileUtil.getData(path);
            if (in != null) {
                /*
                 * 如果偏移量大于最大字节数限制，那么就只截取最大字节数长度的数据
                 */
                int buffSize;
                if (offset > maxFileSize) {
                    buffSize = (int) maxFileSize;
                } else {
                    buffSize = (int) offset;
                }

                in.skip(startIndex);
                byte[] buff = new byte[buffSize];
                StringBuilder sb = new StringBuilder();
                int len;
                long endPoint = 0;
                while ((len = in.read(buff)) != -1) {
                    endPoint += len;
                    if (endPoint > offset) {
                        len = (int) (len - (endPoint - offset));
                    }
                    sb.append(new String(buff, 0, len, StandardCharsets.UTF_8));
                    if (endPoint > offset) {
                        break;
                    }
                }
                result = sb.toString();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    public static void downLoadAuditDetail(HttpServletRequest request, HttpServletResponse response, String filePath) {
        if (StringUtils.isBlank(filePath)) {
            throw new FilePathIllegalException("文件路径不能为空");
        }
        if (!filePath.contains("?") || !filePath.contains("&") || !filePath.contains("=")) {
            throw new FilePathIllegalException("文件路径格式错误");
        }
        String path = filePath.split("\\?")[0];
        String[] indexs = filePath.split("\\?")[1].split("&");
        long startIndex = Long.parseLong(indexs[0].split("=")[1]);
        long offset = Long.parseLong(indexs[1].split("=")[1]);

        try (InputStream in = FileUtil.getData(path)) {
            if (in != null) {
                in.skip(startIndex);

                String fileNameEncode = "AUDIT_DETAIL.log";
                boolean flag = request.getHeader("User-Agent").indexOf("Gecko") > 0;
                if (request.getHeader("User-Agent").toLowerCase().indexOf("msie") > 0 || flag) {
                    fileNameEncode = URLEncoder.encode(fileNameEncode, "UTF-8");// IE浏览器
                } else {
                    fileNameEncode = new String(fileNameEncode.getBytes(StandardCharsets.UTF_8), "ISO8859-1");
                }
                response.setContentType("application/x-msdownload;charset=utf-8");
                response.setHeader("Content-Disposition", " attachment; filename=\"" + fileNameEncode + "\"");
                OutputStream os = response.getOutputStream();

                byte[] buff = new byte[(int) maxFileSize];
                int len;
                long endPoint = 0;
                while ((len = in.read(buff)) != -1) {
                    /*
                     * endPoint用来记录累计读取到的字节数
                     * 如果大于偏移量，说明实际读到的数据超过了需要的数据
                     * 那么就需要减掉多读出来的数据
                     */
                    endPoint += len;
                    if (endPoint > offset) {
                        len = (int) (len - (endPoint - offset));
                    }
                    os.write(buff, 0, len);
                    os.flush();
                    if (endPoint > offset) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
