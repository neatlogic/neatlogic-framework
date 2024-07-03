/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.module.framework.service;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.SystemUser;
import neatlogic.framework.common.util.FileUtil;
import neatlogic.framework.crossover.IFileCrossoverService;
import neatlogic.framework.exception.file.FileAccessDeniedException;
import neatlogic.framework.exception.file.FileNotFoundException;
import neatlogic.framework.exception.file.FilePathIllegalException;
import neatlogic.framework.exception.file.FileTypeHandlerNotFoundException;
import neatlogic.framework.exception.user.NoTenantException;
import neatlogic.framework.file.core.FileOperationType;
import neatlogic.framework.file.core.FileTypeHandlerFactory;
import neatlogic.framework.file.core.IFileTypeHandler;
import neatlogic.framework.file.dao.mapper.FileMapper;
import neatlogic.framework.file.dto.FileVo;
import neatlogic.framework.heartbeat.dao.mapper.ServerMapper;
import neatlogic.framework.heartbeat.dto.ServerClusterVo;
import neatlogic.framework.integration.authentication.enums.AuthenticateType;
import neatlogic.framework.util.HttpRequestUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
public class FileServiceImpl implements IFileCrossoverService {
    static Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    /*查看审计记录时可显示的最大字节数，超过此数需要下载文件后查看*/
    private static final int MAX_FILE_SIZE = 1024 * 1024;

    @Resource
    private ServerMapper serverMapper;

    @Resource
    private FileMapper fileMapper;

    @Override
    public FileVo getFileById(Long id) {
        return fileMapper.getFileById(id);
    }

    @Override
    public void downloadFile(JSONObject paramObj, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long id = paramObj.getLong("id");
        FileVo fileVo = fileMapper.getFileById(id);
        if (fileVo == null) {
            throw new FileNotFoundException(id);
        }
        String tenantUuid = TenantContext.get().getTenantUuid();
        if (StringUtils.isBlank(tenantUuid)) {
            throw new NoTenantException();
        }
        BigDecimal lastModified = null;
        if (paramObj.getDouble("lastModified") != null) {
            lastModified = new BigDecimal(Double.toString(paramObj.getDouble("lastModified")));
        }
        if (lastModified != null) {
            if (lastModified.multiply(new BigDecimal("1000")).longValue() >= fileVo.getUploadTime().getTime()) {
                HttpServletResponse resp = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
                if (resp != null) {
                    resp.setStatus(205);
//                    resp.getWriter().print(StringUtils.EMPTY);
                }
            }
        }
        if (fileVo != null) {
            IFileTypeHandler fileTypeHandler = FileTypeHandlerFactory.getHandler(fileVo.getType());
            if (fileTypeHandler != null) {
                //system 用户下载权限豁免
                if (Objects.equals(UserContext.get().getUserUuid(), SystemUser.SYSTEM.getUserUuid()) || fileTypeHandler.valid(UserContext.get().getUserUuid(), fileVo, paramObj)) {
                    ServletOutputStream os = null;
                    InputStream in = null;
                    in = FileUtil.getData(fileVo.getPath());
                    if (in != null) {
                        String fileNameEncode = "";
                        Boolean flag = request.getHeader("User-Agent").indexOf("Gecko") > 0;
                        if (request.getHeader("User-Agent").toLowerCase().indexOf("msie") > 0 || flag) {
                            fileNameEncode = URLEncoder.encode(fileVo.getName(), "UTF-8");// IE浏览器
                        } else {
                            fileNameEncode = new String(fileVo.getName().replace(" ", "").getBytes(StandardCharsets.UTF_8), "ISO8859-1");
                        }

                        if (StringUtils.isBlank(fileVo.getContentType())) {
                            response.setContentType("application/x-msdownload");
                        } else {
                            response.setContentType(fileVo.getContentType());
                        }
                        response.setHeader("Content-Disposition", " attachment; filename=\"" + fileNameEncode + "\"");
                        os = response.getOutputStream();
                        IOUtils.copyLarge(in, os);
                        if (os != null) {
                            os.flush();
                            os.close();
                        }
                        if (in != null) {
                            in.close();
                        }
                    }
                } else {
                    throw new FileAccessDeniedException(fileVo.getName(), FileOperationType.DOWNLOAD.getText());
                }
            } else {
                throw new FileTypeHandlerNotFoundException(fileVo.getType());
            }
        } else {
            throw new FileNotFoundException(id);
        }
    }

    @Override
    public void deleteFile(Long fileId, JSONObject paramObj) throws Exception {
        FileVo fileVo = fileMapper.getFileById(fileId);
        if (fileVo == null) {
            return;
        }
        IFileTypeHandler fileTypeHandler = FileTypeHandlerFactory.getHandler(fileVo.getType());
        if (fileTypeHandler == null) {
            return;
        }
        if (fileTypeHandler.valid(UserContext.get().getUserUuid(), fileVo, paramObj)) {
            fileTypeHandler.deleteFile(fileVo, paramObj);
        }
    }


    @Override
    public JSONObject readLocalFile(String path, int startIndex, int offset) {
        String dataHome = Config.DATA_HOME() + TenantContext.get().getTenantUuid();
        String prefix = "${home}";
        if (path.startsWith(prefix)) {
            path = path.substring(prefix.length());
            path = dataHome + path;
        } else {
            throw new FilePathIllegalException(path);
        }
        if (!path.startsWith("file:")) {
            path = "file:" + path;
        }
        JSONObject resultObj = new JSONObject();
        boolean hasMore = false;
        /*
         * 如果偏移量大于最大字节数限制，那么就只截取最大字节数长度的数据
         */
        if (offset > MAX_FILE_SIZE) {
            offset = MAX_FILE_SIZE;
            hasMore = true;
        }
        resultObj.put("hasMore", hasMore);
        try (InputStream in = FileUtil.getData(path)) {
            if (in != null) {
                in.skip(startIndex);
                byte[] buff = new byte[1024];
                StringBuilder sb = new StringBuilder();
                int len;
                int endPoint = 0;
                while ((len = in.read(buff)) != -1) {
                    endPoint += len;
                    if (endPoint >= offset) {
                        len = (len - (endPoint - offset));
                        sb.append(new String(buff, 0, len, StandardCharsets.UTF_8));
                        break;
                    } else {
                        sb.append(new String(buff, 0, len, StandardCharsets.UTF_8));
                    }
                }
                resultObj.put("content", sb.toString());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return resultObj;
    }

    @Override
    public JSONObject readRemoteFile(JSONObject paramObj, Integer serverId) {
        JSONObject resultObj = new JSONObject();
        String host = null;
        TenantContext.get().setUseDefaultDatasource(true);
        ServerClusterVo serverClusterVo = serverMapper.getServerByServerId(serverId);
        if (serverClusterVo != null) {
            host = serverClusterVo.getHost();
        }
        TenantContext.get().setUseDefaultDatasource(false);
        if (StringUtils.isBlank(host)) {
            return resultObj;
        }
        HttpServletRequest request = RequestContext.get().getRequest();
        String url = host + request.getRequestURI();
        HttpRequestUtil httpRequestUtil = HttpRequestUtil.post(url)
                .setPayload(paramObj.toJSONString())
                .setAuthType(AuthenticateType.BUILDIN)
                .setConnectTimeout(5000)
                .setReadTimeout(5000)
                .sendRequest();
        String error = httpRequestUtil.getError();
        if (StringUtils.isNotBlank(error)) {
            throw new RuntimeException(error);
        }
        JSONObject resultJson = httpRequestUtil.getResultJson();
        if (MapUtils.isNotEmpty(resultJson)) {
            String status = resultJson.getString("Status");
            if (!"OK".equals(status)) {
                throw new RuntimeException(resultJson.getString("Message"));
            }
            resultObj = resultJson.getJSONObject("Return");
        }
        return resultObj;
    }

    @Override
    public void downloadLocalFile(String path, int startIndex, int offset, HttpServletResponse response) {
        String dataHome = Config.DATA_HOME() + TenantContext.get().getTenantUuid();
        String prefix = "${home}";
        if (path.startsWith(prefix)) {
            path = path.substring(prefix.length());
            path = dataHome + path;
        } else {
            throw new FilePathIllegalException(path);
        }
        if (!path.startsWith("file:")) {
            path = "file:" + path;
        }
        try (InputStream in = FileUtil.getData(path)) {
            if (in != null) {
                in.skip(startIndex);
                String fileNameEncode = neatlogic.framework.util.FileUtil.getEncodedFileName("AUDIT_DETAIL.log");
                response.setContentType("application/x-msdownload;charset=utf-8");
                response.setHeader("Content-Disposition", " attachment; filename=\"" + fileNameEncode + "\"");
                OutputStream os = response.getOutputStream();

                byte[] buff = new byte[1024];
                int len;
                int endPoint = 0;
                while ((len = in.read(buff)) != -1) {
                    endPoint += len;
                    if (endPoint >= offset) {
                        len = (len - (endPoint - offset));
                    }
                    os.write(buff, 0, len);
                    os.flush();
                    if (endPoint >= offset) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void downloadRemoteFile(JSONObject paramObj, Integer serverId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String host = null;
        TenantContext.get().setUseDefaultDatasource(true);
        ServerClusterVo serverClusterVo = serverMapper.getServerByServerId(serverId);
        if (serverClusterVo != null) {
            host = serverClusterVo.getHost();
        }
        TenantContext.get().setUseDefaultDatasource(false);
        if (StringUtils.isBlank(host)) {
            return;
        }
        String url = host + request.getRequestURI();
        HttpRequestUtil httpRequestUtil = HttpRequestUtil.download(url, "POST", response.getOutputStream()).setPayload(paramObj.toJSONString()).setAuthType(AuthenticateType.BUILDIN).sendRequest();
        String error = httpRequestUtil.getError();
        if (StringUtils.isNotBlank(error)) {
            throw new RuntimeException(error);
        }
    }
}
