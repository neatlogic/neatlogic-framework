/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.module.framework.service;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.constvalue.SystemUser;
import neatlogic.framework.common.util.FileUtil;
import neatlogic.framework.crossover.IFileCrossoverService;
import neatlogic.framework.exception.file.FileAccessDeniedException;
import neatlogic.framework.exception.file.FileNotFoundException;
import neatlogic.framework.exception.file.FileTypeHandlerNotFoundException;
import neatlogic.framework.exception.user.NoTenantException;
import neatlogic.framework.file.core.FileTypeHandlerFactory;
import neatlogic.framework.file.core.IFileTypeHandler;
import neatlogic.framework.file.dao.mapper.FileMapper;
import neatlogic.framework.file.dto.FileVo;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
public class FileServiceImpl implements FileService, IFileCrossoverService {

    @Resource
    private FileMapper fileMapper;

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
                    throw new FileAccessDeniedException(fileVo.getName(), "下载");
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
}
