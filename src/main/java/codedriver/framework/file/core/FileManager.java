/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.file.core;

import codedriver.framework.common.util.FileUtil;
import codedriver.framework.file.dao.mapper.FileMapper;
import codedriver.framework.file.dto.FileVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class FileManager implements IFileManager {
    @Autowired
    private ApplicationContext applicationContext;

    @Resource
    private FileMapper fileMapper;

    private static IFileManager fileManager;

    @PostConstruct
    public void init() {
        fileManager = applicationContext.getBean(IFileManager.class);
    }

    public static void deleteFileById(Long fileId) throws Exception {
        fileManager.deleteFile(fileId);
    }

    @Transactional
    public void deleteFile(Long fileId) throws Exception {
        FileVo fileVo = fileMapper.getFileById(fileId);
        fileMapper.deleteFile(fileId);
        FileUtil.deleteData(fileVo.getPath());
    }
}
