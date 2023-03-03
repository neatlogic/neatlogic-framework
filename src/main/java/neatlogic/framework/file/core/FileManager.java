/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.file.core;

import neatlogic.framework.common.util.FileUtil;
import neatlogic.framework.file.dao.mapper.FileMapper;
import neatlogic.framework.file.dto.FileVo;
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
